package com.blocklogic.agritechevolved.block.entity;

import com.blocklogic.agritechevolved.Config;
import com.blocklogic.agritechevolved.block.custom.AdvancedPlanterBlock;
import com.blocklogic.agritechevolved.block.custom.ComposterBlock;
import com.blocklogic.agritechevolved.config.CompostableConfig;
import com.blocklogic.agritechevolved.item.ATEItems;
import com.blocklogic.agritechevolved.screen.custom.ComposterMenu;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ComposterBlockEntity extends BlockEntity implements MenuProvider {

    private static final int INPUT_SLOTS_START = 0;
    private static final int INPUT_SLOTS_COUNT = 12;
    private static final int OUTPUT_SLOTS_START = 12;
    private static final int OUTPUT_SLOTS_COUNT = 3;
    private static final int MODULE_SLOT = 15;

    private int progress = 0;
    private int energyStored = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(16) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            if (slot == MODULE_SLOT) {
                return 1;
            }
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot >= INPUT_SLOTS_START && slot < INPUT_SLOTS_START + INPUT_SLOTS_COUNT) {
                return isCompostable(stack);
            } else if (slot >= OUTPUT_SLOTS_START && slot < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT) {
                return false;
            } else if (slot == MODULE_SLOT) {
                return isSpeedModule(stack);
            }
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public ComposterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.COMPOSTER_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ComposterBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        boolean wasChanged = false;

        int requiredEnergy = Config.getComposterBasePowerConsumption();
        boolean hasPower = blockEntity.energyStored >= requiredEnergy;

        int baseProcessingTime = Config.getComposterBaseProcessingTime();

        if (!hasPower) {
            baseProcessingTime *= 3;
        }

        double speedMultiplier = blockEntity.getModuleSpeedModifier();
        int actualProcessingTime = (int) Math.max(1, baseProcessingTime / speedMultiplier);

        if (blockEntity.canProcess()) {
            if (blockEntity.progress == 0) {
                blockEntity.progress = 1;
                wasChanged = true;
            } else {
                blockEntity.progress++;
                wasChanged = true;
            }

            if (blockEntity.progress >= actualProcessingTime) {
                blockEntity.processItems();

                if (hasPower) {
                    double powerMultiplier = blockEntity.getModulePowerModifier();
                    int adjustedEnergyConsumption = (int) Math.ceil(requiredEnergy * powerMultiplier);
                    blockEntity.energyStored -= adjustedEnergyConsumption;
                }

                blockEntity.progress = 0;
                wasChanged = true;
            }
        } else {
            if (blockEntity.progress > 0) {
                blockEntity.progress = 0;
                wasChanged = true;
            }
        }

        if (wasChanged) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);

            boolean shouldBePowered = blockEntity.progress > 0;
            boolean currentlyPowered = state.getValue(ComposterBlock.POWERED);
            if (shouldBePowered != currentlyPowered) {
                level.setBlock(pos, state.setValue(ComposterBlock.POWERED, shouldBePowered), 3);
            }
        }
    }

    private boolean canProcess() {
        int availableOrganicItems = countAvailableOrganicItems();
        int requiredItems = Config.getComposterItemsPerBiomass();

        if (availableOrganicItems < requiredItems) {
            return false;
        }

        return hasSpaceForBiomass();
    }

    private int countAvailableOrganicItems() {
        int count = 0;
        for (int i = INPUT_SLOTS_START; i < INPUT_SLOTS_START + INPUT_SLOTS_COUNT; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && isCompostable(stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private boolean hasSpaceForBiomass() {
        ItemStack biomassStack = new ItemStack(ATEItems.BIOMASS.get());

        for (int i = OUTPUT_SLOTS_START; i < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT; i++) {
            ItemStack outputStack = inventory.getStackInSlot(i);
            if (outputStack.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItemSameComponents(outputStack, biomassStack) &&
                    outputStack.getCount() < outputStack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void processItems() {
        int requiredItems = Config.getComposterItemsPerBiomass();
        int itemsToConsume = requiredItems;

        for (int i = INPUT_SLOTS_START; i < INPUT_SLOTS_START + INPUT_SLOTS_COUNT && itemsToConsume > 0; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && isCompostable(stack)) {
                int toTake = Math.min(itemsToConsume, stack.getCount());
                stack.shrink(toTake);
                itemsToConsume -= toTake;

                if (stack.isEmpty()) {
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }

        addBiomassToOutput();
    }

    private void addBiomassToOutput() {
        ItemStack biomassStack = new ItemStack(ATEItems.BIOMASS.get(), 1);

        for (int i = OUTPUT_SLOTS_START; i < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT; i++) {
            ItemStack outputStack = inventory.getStackInSlot(i);
            if (outputStack.isEmpty()) {
                inventory.setStackInSlot(i, biomassStack);
                return;
            }
            if (ItemStack.isSameItemSameComponents(outputStack, biomassStack) &&
                    outputStack.getCount() < outputStack.getMaxStackSize()) {
                outputStack.grow(1);
                return;
            }
        }
    }

    private boolean isCompostable(ItemStack stack) {
        if (stack.isEmpty()) return false;

        String itemId = RegistryHelper.getItemId(stack);
        return CompostableConfig.isCompostable(itemId);
    }

    private boolean isSpeedModule(ItemStack stack) {
        if (stack.isEmpty()) return false;

        String itemId = RegistryHelper.getItemId(stack);
        return itemId.equals("agritechevolved:sm_mk1") ||
                itemId.equals("agritechevolved:sm_mk2") ||
                itemId.equals("agritechevolved:sm_mk3");
    }

    private double getModuleSpeedModifier() {
        ItemStack moduleStack = inventory.getStackInSlot(MODULE_SLOT);
        if (moduleStack.isEmpty()) {
            return 1.0;
        }

        String moduleId = RegistryHelper.getItemId(moduleStack);
        return switch (moduleId) {
            case "agritechevolved:sm_mk1" -> Config.getSpeedModuleMk1Multiplier();
            case "agritechevolved:sm_mk2" -> Config.getSpeedModuleMk2Multiplier();
            case "agritechevolved:sm_mk3" -> Config.getSpeedModuleMk3Multiplier();
            default -> 1.0;
        };
    }

    private double getModulePowerModifier() {
        ItemStack moduleStack = inventory.getStackInSlot(MODULE_SLOT);
        if (moduleStack.isEmpty()) {
            return 1.0;
        }

        String moduleId = RegistryHelper.getItemId(moduleStack);
        return switch (moduleId) {
            case "agritechevolved:sm_mk1" -> Config.getSpeedModuleMk1PowerMultiplier();
            case "agritechevolved:sm_mk2" -> Config.getSpeedModuleMk2PowerMultiplier();
            case "agritechevolved:sm_mk3" -> Config.getSpeedModuleMk3PowerMultiplier();
            default -> 1.0;
        };
    }

    public int getEnergyStored() {
        return energyStored;
    }

    public int getMaxEnergyStored() {
        return Config.getComposterEnergyBuffer();
    }

    public boolean canExtractEnergy() {
        return false;
    }

    public boolean canReceiveEnergy() {
        return true;
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(maxReceive, Config.getComposterEnergyBuffer() - energyStored);
        if (!simulate) {
            energyStored += energyReceived;
            setChanged();
        }
        return energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return ComposterBlockEntity.this.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return ComposterBlockEntity.this.extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return ComposterBlockEntity.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return ComposterBlockEntity.this.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return ComposterBlockEntity.this.canExtractEnergy();
            }

            @Override
            public boolean canReceive() {
                return ComposterBlockEntity.this.canReceiveEnergy();
            }
        };
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 16;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if ((slot >= INPUT_SLOTS_START && slot < INPUT_SLOTS_START + INPUT_SLOTS_COUNT) || slot == MODULE_SLOT) {
                    return inventory.insertItem(slot, stack, simulate);
                }
                return stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot >= OUTPUT_SLOTS_START && slot < OUTPUT_SLOTS_START + OUTPUT_SLOTS_COUNT) {
                    return inventory.extractItem(slot, amount, simulate);
                }
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return inventory.isItemValid(slot, stack);
            }
        };
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        int baseProcessingTime = Config.getComposterBaseProcessingTime();

        int requiredEnergy = Config.getComposterBasePowerConsumption();
        boolean hasPower = energyStored >= requiredEnergy;

        if (!hasPower) {
            baseProcessingTime *= 4;
        }

        double speedMultiplier = getModuleSpeedModifier();
        return (int) Math.max(1, baseProcessingTime / speedMultiplier);
    }

    public int getOrganicItemsCollected() {
        return countAvailableOrganicItems();
    }

    public int getRequiredOrganicItems() {
        return Config.getComposterItemsPerBiomass();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("energyStored", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        energyStored = tag.getInt("energyStored");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.composter");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ComposterMenu(containerId, playerInventory, this);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ATEBlockEntities.COMPOSTER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof ComposterBlockEntity composterBlockEntity) {
                        return composterBlockEntity.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.COMPOSTER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof ComposterBlockEntity composterBlockEntity) {
                        return composterBlockEntity.getEnergyStorage(direction);
                    }
                    return null;
                });
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.invalidateCapabilities(getBlockPos());
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            level.invalidateCapabilities(getBlockPos());
        }
    }
}