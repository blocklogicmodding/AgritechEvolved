package com.blocklogic.agritechevolved.block.entity;

import com.blocklogic.agritechevolved.Config;
import com.blocklogic.agritechevolved.block.custom.BiomassBurnerBlock;
import com.blocklogic.agritechevolved.screen.custom.BiomassBurnerMenu;
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
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class BiomassBurnerBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            String itemId = RegistryHelper.getItemId(stack);
            return itemId.equals("agritechevolved:biomass") ||
                    itemId.equals("agritechevolved:compacted_biomass");
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private class GeneratorEnergyStorage extends EnergyStorage {
        public GeneratorEnergyStorage(int capacity) {
            super(capacity, 0, Integer.MAX_VALUE);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public boolean canReceive() {
            return false;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        public int generateEnergy(int amount) {
            int availableSpace = capacity - energy;
            int generated = Math.min(amount, availableSpace);
            energy += generated;
            return generated;
        }
    }

    private final GeneratorEnergyStorage energyStorage = new GeneratorEnergyStorage(Config.getBurnerEnergyBuffer());

    private int progress = 0;
    private int maxProgress = 0;
    private int currentBurnValue = 0;
    private boolean isBurning = false;

    public BiomassBurnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.BURNER_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BiomassBurnerBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) return;

        boolean wasChanged = false;

        if (!blockEntity.isBurning && blockEntity.canStartBurning()) {
            blockEntity.startBurning();
            wasChanged = true;
        }

        if (blockEntity.isBurning) {

            if (blockEntity.energyStorage.getEnergyStored() >= blockEntity.energyStorage.getMaxEnergyStored()) {

            } else {
                blockEntity.progress++;

                if (blockEntity.currentBurnValue > 0) {
                    int generated = blockEntity.energyStorage.generateEnergy(blockEntity.currentBurnValue);
                    if (generated > 0) {
                        wasChanged = true;
                    }
                }

                if (blockEntity.progress >= blockEntity.maxProgress) {
                    blockEntity.completeBurning();
                    wasChanged = true;
                }
            }
        }

        boolean energyDistributed = blockEntity.distributeEnergy();
        if (energyDistributed) {
            wasChanged = true;
        }

        if (wasChanged) {
            blockEntity.setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }

        boolean shouldBeBurning = blockEntity.isBurning();
        boolean currentlyBurning = state.getValue(BiomassBurnerBlock.BURNING);

        if (shouldBeBurning != currentlyBurning) {
            level.setBlock(pos, state.setValue(BiomassBurnerBlock.BURNING, shouldBeBurning), 3);
        }
    }

    private boolean canStartBurning() {
        ItemStack fuelStack = inventory.getStackInSlot(0);
        if (fuelStack.isEmpty()) return false;

        if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
            return false;
        }

        String itemId = RegistryHelper.getItemId(fuelStack);
        return itemId.equals("agritechevolved:biomass") ||
                itemId.equals("agritechevolved:compacted_biomass") ||
                itemId.equals("agritechevolved:crude_biomass");
    }

    private void startBurning() {
        ItemStack fuelStack = inventory.getStackInSlot(0);
        if (fuelStack.isEmpty()) return;

        String itemId = RegistryHelper.getItemId(fuelStack);

        int baseRF = 0;
        int burnDuration = 0;
        int baseDuration = 0;

        switch (itemId) {
            case "agritechevolved:biomass" -> {
                baseRF = Config.getBurnerBiomassRfValue();
                burnDuration = Config.getBurnerBiomassBurnDuration();
                baseDuration = 100;
            }
            case "agritechevolved:compacted_biomass" -> {
                baseRF = Config.getBurnerCompactedBiomassRfValue();
                burnDuration = Config.getBurnerCompactedBiomassBurnDuration();
                baseDuration = 180;
            }
            case "agritechevolved:crude_biomass" -> {
                baseRF = Config.getBurnerCrudeBiomassRfValue();
                burnDuration = Config.getBurnerCrudeBiomassBurnDuration();
                baseDuration = 50;
            }
            default -> {
                return; // Unknown fuel type
            }
        }

        int totalRF = (int) (baseRF * ((float) burnDuration / baseDuration));

        if (totalRF <= 0 || burnDuration <= 0) {
            return;
        }

        maxProgress = burnDuration;
        currentBurnValue = totalRF / maxProgress;

        fuelStack.shrink(1);
        inventory.setStackInSlot(0, fuelStack);

        progress = 0;
        isBurning = true;
    }

    private void completeBurning() {
        progress = 0;
        maxProgress = 0;
        currentBurnValue = 0;
        isBurning = false;
    }

    private boolean distributeEnergy() {
        if (energyStorage.getEnergyStored() <= 0) return false;

        boolean energyDistributed = false;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            if (neighborBE != null) {
                IEnergyStorage neighborEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy.canReceive()) {
                    int energyToTransfer = energyStorage.extractEnergy(1000, true);
                    if (energyToTransfer > 0) {
                        int transferred = neighborEnergy.receiveEnergy(energyToTransfer, false);
                        if (transferred > 0) {
                            energyStorage.extractEnergy(transferred, false);
                            energyDistributed = true;
                        }
                    }
                }
            }
        }

        return energyDistributed;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isBurning() {
        return isBurning;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.put("energy", energyStorage.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putInt("currentBurnValue", currentBurnValue);
        tag.putBoolean("isBurning", isBurning);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        currentBurnValue = tag.getInt("currentBurnValue");
        isBurning = tag.getBoolean("isBurning");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public boolean canExtractEnergy() {
        return true;
    }

    public boolean canReceiveEnergy() {
        return false;
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract, simulate);
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BiomassBurnerBlockEntity.this.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return BiomassBurnerBlockEntity.this.extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BiomassBurnerBlockEntity.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BiomassBurnerBlockEntity.this.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return BiomassBurnerBlockEntity.this.canExtractEnergy();
            }

            @Override
            public boolean canReceive() {
                return BiomassBurnerBlockEntity.this.canReceiveEnergy();
            }
        };
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.getStackInSlot(slot);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (stack.isEmpty()) return stack;
                String itemId = RegistryHelper.getItemId(stack);
                if (itemId.equals("agritechevolved:biomass") || itemId.equals("agritechevolved:compacted_biomass")) {
                    return inventory.insertItem(slot, stack, simulate);
                }
                return stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return inventory.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return inventory.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (stack.isEmpty()) return false;
                String itemId = RegistryHelper.getItemId(stack);
                return itemId.equals("agritechevolved:biomass") ||
                        itemId.equals("agritechevolved:compacted_biomass") ||
                        itemId.equals("agritechevolved:crude_biomass");
            }
        };
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ATEBlockEntities.BURNER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof BiomassBurnerBlockEntity burnerBlockEntity) {
                        return burnerBlockEntity.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.BURNER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof BiomassBurnerBlockEntity burnerBlockEntity) {
                        return burnerBlockEntity.getEnergyStorage(direction);
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.biomass_burner");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BiomassBurnerMenu(containerId, playerInventory, this);
    }
}