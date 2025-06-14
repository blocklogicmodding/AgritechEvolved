package com.blocklogic.agritechevolved.block.entity;

import com.blocklogic.agritechevolved.Config;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.screen.custom.BasicPlanterMenu;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BasicPlanterBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(14) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            if (slot == 0 || slot == 1) {
                return 1;
            }
            return super.getStackLimit(slot, stack);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            String itemId = RegistryHelper.getItemId(stack);

            switch (slot) {
                case 0:
                    return PlantablesConfig.isValidSeed(itemId) || PlantablesConfig.isValidSapling(itemId);
                case 1:
                    return PlantablesConfig.isValidSoil(itemId);
                default:
                    return false;
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private int growthProgress = 0;
    private int growthTicks = 0;
    private boolean readyToHarvest = false;
    private int lastGrowthStage = -1;

    public BasicPlanterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.BASIC_PLANTER_BE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BasicPlanterBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        ItemStack plantStack = blockEntity.inventory.getStackInSlot(0);
        ItemStack soilStack = blockEntity.inventory.getStackInSlot(1);

        if (plantStack.isEmpty() || soilStack.isEmpty()) {
            blockEntity.resetGrowth();
            return;
        }

        String plantId = RegistryHelper.getItemId(plantStack);
        String soilId = RegistryHelper.getItemId(soilStack);

        if (!blockEntity.isValidPlantSoilCombination(plantId, soilId)) {
            blockEntity.resetGrowth();
            return;
        }

        if (!blockEntity.readyToHarvest) {
            float soilModifier = blockEntity.getSoilGrowthModifier(soilStack);

            int baseGrowthTime = blockEntity.getBaseGrowthTime(plantStack);
            int adjustedGrowthTime = Math.max(1, Math.round(baseGrowthTime / soilModifier));

            blockEntity.growthTicks++;

            if (blockEntity.growthTicks >= adjustedGrowthTime) {
                blockEntity.readyToHarvest = true;
                blockEntity.growthProgress = 100;
                blockEntity.lastGrowthStage = blockEntity.getGrowthStage();

                level.sendBlockUpdated(pos, state, state, 3);
                blockEntity.setChanged();
            } else {
                blockEntity.growthProgress = (int)((blockEntity.growthTicks / (float)adjustedGrowthTime) * 100);

                int currentGrowthStage = blockEntity.getGrowthStage();
                if (currentGrowthStage != blockEntity.lastGrowthStage) {
                    blockEntity.lastGrowthStage = currentGrowthStage;
                }

                if (blockEntity.growthTicks % 20 == 0) {
                    level.sendBlockUpdated(pos, state, state, 3);
                    blockEntity.setChanged();
                }
            }
        }

        if (blockEntity.readyToHarvest && blockEntity.hasOutputSpace()) {
            blockEntity.harvestPlant();
        }

        tryOutputItemsBelow(level, pos, blockEntity);
    }

    private boolean isValidPlantSoilCombination(String plantId, String soilId) {
        if (PlantablesConfig.isValidSeed(plantId)) {
            return PlantablesConfig.isSoilValidForSeed(soilId, plantId);
        } else if (PlantablesConfig.isValidSapling(plantId)) {
            return PlantablesConfig.isSoilValidForSapling(soilId, plantId);
        }
        return false;
    }

    private boolean isTree() {
        ItemStack plantStack = inventory.getStackInSlot(0);
        if (plantStack.isEmpty()) return false;

        String itemId = RegistryHelper.getItemId(plantStack);
        return PlantablesConfig.isValidSapling(itemId);
    }

    private boolean isCrop() {
        ItemStack plantStack = inventory.getStackInSlot(0);
        if (plantStack.isEmpty()) return false;

        String itemId = RegistryHelper.getItemId(plantStack);
        return PlantablesConfig.isValidSeed(itemId);
    }

    private int getBaseGrowthTime(ItemStack plantStack) {
        String itemId = RegistryHelper.getItemId(plantStack);

        if (PlantablesConfig.isValidSeed(itemId)) {
            return Config.getPlanterBaseProcessingTime();
        } else if (PlantablesConfig.isValidSapling(itemId)) {
            return PlantablesConfig.getBaseSaplingGrowthTime(itemId);
        }

        return Config.getPlanterBaseProcessingTime();
    }

    public float getSoilGrowthModifier(ItemStack soilStack) {
        if (soilStack.isEmpty()) return 1.0f;

        String soilId = RegistryHelper.getItemId(soilStack);
        return PlantablesConfig.getSoilGrowthModifier(soilId);
    }

    private void resetGrowth() {
        growthProgress = 0;
        growthTicks = 0;
        readyToHarvest = false;
        lastGrowthStage = -1;
        setChanged();
    }

    public void harvestPlant() {
        if (!readyToHarvest) return;

        ItemStack plantStack = inventory.getStackInSlot(0);
        List<ItemStack> drops = getHarvestDrops(plantStack);

        for (ItemStack drop : drops) {
            int remainingItemsToPlace = drop.getCount();

            for (int slot = 2; slot <= 13; slot++) {
                ItemStack existingStack = inventory.getStackInSlot(slot);

                if (existingStack.isEmpty()) {
                    int itemsToPlace = Math.min(remainingItemsToPlace, drop.getMaxStackSize());
                    inventory.setStackInSlot(slot, new ItemStack(drop.getItem(), itemsToPlace));
                    remainingItemsToPlace -= itemsToPlace;
                } else if (existingStack.is(drop.getItem()) &&
                        existingStack.getCount() < existingStack.getMaxStackSize()) {
                    int spaceAvailable = existingStack.getMaxStackSize() - existingStack.getCount();
                    int itemsToAdd = Math.min(spaceAvailable, remainingItemsToPlace);
                    existingStack.grow(itemsToAdd);
                    remainingItemsToPlace -= itemsToAdd;
                }

                if (remainingItemsToPlace <= 0) {
                    break;
                }
            }

            if (remainingItemsToPlace > 0) {
                break;
            }
        }

        resetGrowth();
    }

    private List<ItemStack> getHarvestDrops(ItemStack plantStack) {
        List<ItemStack> drops = new ArrayList<>();
        Random random = new Random();

        if (plantStack.isEmpty()) return drops;

        String plantId = RegistryHelper.getItemId(plantStack);
        List<PlantablesConfig.DropInfo> configDrops;

        if (PlantablesConfig.isValidSeed(plantId)) {
            configDrops = PlantablesConfig.getCropDrops(plantId);
        } else if (PlantablesConfig.isValidSapling(plantId)) {
            configDrops = PlantablesConfig.getTreeDrops(plantId);
        } else {
            return drops;
        }

        for (PlantablesConfig.DropInfo dropInfo : configDrops) {
            if (random.nextFloat() <= dropInfo.chance) {
                int count = dropInfo.minCount;
                if (dropInfo.maxCount > dropInfo.minCount) {
                    count = dropInfo.minCount + random.nextInt(dropInfo.maxCount - dropInfo.minCount + 1);
                }

                Item item = RegistryHelper.getItem(dropInfo.item);
                if (item != null) {
                    drops.add(new ItemStack(item, count));
                }
            }
        }

        return drops;
    }

    private static void tryOutputItemsBelow(Level level, BlockPos pos, BasicPlanterBlockEntity blockEntity) {
        BlockPos belowPos = pos.below();
        BlockEntity targetEntity = level.getBlockEntity(belowPos);

        if (targetEntity == null) {
            return;
        }

        IItemHandler targetInventory = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                belowPos,
                Direction.UP);

        if (targetInventory == null) {
            return;
        }

        boolean changed = false;

        for (int slot = 2; slot <= 13; slot++) {
            if (!blockEntity.inventory.getStackInSlot(slot).isEmpty()) {
                var extractedItem = blockEntity.inventory.extractItem(slot, 64, true);

                if (!extractedItem.isEmpty()) {
                    var remaining = ItemHandlerHelper.insertItemStacked(targetInventory, extractedItem, false);
                    int insertedAmount = extractedItem.getCount() - remaining.getCount();

                    if (insertedAmount > 0) {
                        blockEntity.inventory.extractItem(slot, insertedAmount, false);
                        changed = true;
                    }
                }
            }
        }

        if (changed) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    public boolean hasOutputSpace() {
        List<ItemStack> potentialDrops = getHarvestDrops(inventory.getStackInSlot(0));

        Map<Integer, ItemStack> simulatedSlots = new HashMap<>();
        for (int slot = 2; slot <= 13; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            simulatedSlots.put(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }

        for (ItemStack drop : potentialDrops) {
            int remainingToPlace = drop.getCount();

            for (int slot = 2; slot <= 13; slot++) {
                ItemStack existingStack = simulatedSlots.get(slot);
                if (!existingStack.isEmpty() && existingStack.is(drop.getItem()) &&
                        existingStack.getCount() < existingStack.getMaxStackSize()) {

                    int spaceAvailable = existingStack.getMaxStackSize() - existingStack.getCount();
                    int itemsToAdd = Math.min(spaceAvailable, remainingToPlace);

                    existingStack.grow(itemsToAdd);
                    remainingToPlace -= itemsToAdd;

                    if (remainingToPlace <= 0) {
                        break;
                    }
                }
            }

            if (remainingToPlace > 0) {
                for (int slot = 2; slot <= 13; slot++) {
                    ItemStack existingStack = simulatedSlots.get(slot);
                    if (existingStack.isEmpty()) {
                        simulatedSlots.put(slot, new ItemStack(drop.getItem(), remainingToPlace));
                        remainingToPlace = 0;
                        break;
                    }
                }
            }

            if (remainingToPlace > 0) {
                return false;
            }
        }

        return true;
    }

    public float getGrowthProgress() {
        return growthProgress / 100f;
    }

    public int getGrowthStage() {
        if (isTree()) {
            return growthProgress > 50 ? 1 : 0;
        } else {
            return Math.min(8, (int)(growthProgress / 12.5f));
        }
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("growthProgress", growthProgress);
        tag.putInt("growthTicks", growthTicks);
        tag.putBoolean("readyToHarvest", readyToHarvest);
        tag.putInt("lastGrowthStage", lastGrowthStage);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        growthProgress = tag.getInt("growthProgress");
        growthTicks = tag.getInt("growthTicks");
        readyToHarvest = tag.getBoolean("readyToHarvest");
        lastGrowthStage = tag.getInt("lastGrowthStage");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.basic_planter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BasicPlanterMenu(i, inventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}