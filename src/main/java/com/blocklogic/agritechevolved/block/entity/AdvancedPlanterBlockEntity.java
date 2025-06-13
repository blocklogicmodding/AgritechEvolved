package com.blocklogic.agritechevolved.block.entity;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.screen.custom.AdvancedPlanterMenu;
import com.blocklogic.agritechevolved.util.ATETags;
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
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AdvancedPlanterBlockEntity extends BlockEntity implements MenuProvider {

    private class OutputOnlyItemHandler implements IItemHandler {
        private final ItemStackHandler original;
        private final int firstOutputSlot;
        private final int lastOutputSlot;

        public OutputOnlyItemHandler(ItemStackHandler original, int firstOutputSlot, int lastOutputSlot) {
            this.original = original;
            this.firstOutputSlot = firstOutputSlot;
            this.lastOutputSlot = lastOutputSlot;
        }

        @Override
        public int getSlots() {
            return original.getSlots();
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return original.getStackInSlot(slot);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot >= firstOutputSlot && slot <= lastOutputSlot) {
                return original.extractItem(slot, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return original.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    }

    public final ItemStackHandler inventory = new ItemStackHandler(17) {
        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0 || slot == 1 || slot == 2 || slot == 3 || slot == 4) {
                return 1;
            }
            return super.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            String itemId = RegistryHelper.getItemId(stack);

            switch (slot) {
                case 0:
                    if (!PlantablesConfig.isValidSeed(itemId) && !PlantablesConfig.isValidSapling(itemId)) {
                        return false;
                    }

                    ItemStack existingSoil = getStackInSlot(1);
                    if (!existingSoil.isEmpty()) {
                        String soilId = RegistryHelper.getItemId(existingSoil);
                        if (PlantablesConfig.isValidSeed(itemId)) {
                            return PlantablesConfig.isSoilValidForSeed(soilId, itemId);
                        } else if (PlantablesConfig.isValidSapling(itemId)) {
                            return PlantablesConfig.isSoilValidForSapling(soilId, itemId);
                        }
                    }
                    return true;

                case 1:
                    if (!PlantablesConfig.isValidSoil(itemId)) {
                        return false;
                    }

                    ItemStack existingPlant = getStackInSlot(0);
                    if (!existingPlant.isEmpty()) {
                        String plantId = RegistryHelper.getItemId(existingPlant);
                        if (PlantablesConfig.isValidSeed(plantId)) {
                            return PlantablesConfig.isSoilValidForSeed(itemId, plantId);
                        } else if (PlantablesConfig.isValidSapling(plantId)) {
                            return PlantablesConfig.isSoilValidForSapling(itemId, plantId);
                        }
                    }
                    return true;

                case 2:
                case 3:
                    return stack.is(ATETags.Items.ATE_MODULES);

                case 4:
                    return PlantablesConfig.isValidFertilizer(itemId);

                case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 12:
                case 13: case 14: case 15: case 16:
                    return false;

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

    private final OutputOnlyItemHandler outputHandler;

    public IItemHandler getOutputHandler() {
        return outputHandler;
    }

    public AdvancedPlanterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.PLANTER_BE.get(), pos, blockState);
        this.outputHandler = new OutputOnlyItemHandler(inventory, 5, 16);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.agritechevolved.advanced_planter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new AdvancedPlanterMenu(i, inventory, this);
    }

    private int growthProgress = 0;
    private int growthTicks = 0;
    private boolean readyToHarvest = false;
    private static final int BASE_POWER_CONSUMPTION = 128;
    private static final int ENERGY_CAPACITY = 20000;
    private int energyStored = 0;

    public int getEnergyStored() {
        return energyStored;
    }

    public int getMaxEnergyStored() {
        return ENERGY_CAPACITY;
    }

    public boolean canExtractEnergy() {
        return false;
    }

    public boolean canReceiveEnergy() {
        return true;
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(maxReceive, ENERGY_CAPACITY - energyStored);
        if (!simulate) {
            energyStored += energyReceived;
            setChanged();
        }
        return energyReceived;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    private boolean consumeEnergy() {
        float powerModifier = getModulePowerModifier();
        int powerRequired = Math.round(BASE_POWER_CONSUMPTION * powerModifier);

        if (energyStored >= powerRequired) {
            energyStored -= powerRequired;
            setChanged();
            return true;
        }
        return false;
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return AdvancedPlanterBlockEntity.this.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return AdvancedPlanterBlockEntity.this.extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return AdvancedPlanterBlockEntity.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return AdvancedPlanterBlockEntity.this.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return AdvancedPlanterBlockEntity.this.canExtractEnergy();
            }

            @Override
            public boolean canReceive() {
                return AdvancedPlanterBlockEntity.this.canReceiveEnergy();
            }
        };
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == Direction.DOWN) {
            return outputHandler;
        } else {
            return inventory;
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ATEBlockEntities.PLANTER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof AdvancedPlanterBlockEntity planterBlockEntity) {
                        return planterBlockEntity.getItemHandler(direction) ;
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.PLANTER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof AdvancedPlanterBlockEntity planterBlockEntity) {
                        return planterBlockEntity.getEnergyStorage(direction);
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

    private float getModuleSpeedModifier() {
        float speedModifier = 1.0f;
        float speedReduction = 1.0f;
        boolean hasSpeedModule = false;
        boolean hasPowerEfficiencyModule = false;
        boolean hasYieldModule = false;

        for (int slot = 2; slot <= 3; slot++) {
            ItemStack moduleStack = inventory.getStackInSlot(slot);
            if (!moduleStack.isEmpty()) {
                String moduleId = RegistryHelper.getItemId(moduleStack);

                if (moduleId.equals("agritechevolved:sm_mk1")) {
                    speedModifier *= 1.1f;
                    hasSpeedModule = true;
                } else if (moduleId.equals("agritechevolved:sm_mk2")) {
                    speedModifier *= 1.25f;
                    hasSpeedModule = true;
                } else if (moduleId.equals("agritechevolved:sm_mk3")) {
                    speedModifier *= 1.5f;
                    hasSpeedModule = true;
                }
                else if (moduleId.equals("agritechevolved:pem_mk1")) {
                    speedReduction *= 0.95f;
                    hasPowerEfficiencyModule = true;
                } else if (moduleId.equals("agritechevolved:pem_mk2")) {
                    speedReduction *= 0.9f;
                    hasPowerEfficiencyModule = true;
                } else if (moduleId.equals("agritechevolved:pem_mk3")) {
                    speedReduction *= 0.85f;
                    hasPowerEfficiencyModule = true;
                }
                else if (moduleId.equals("agritechevolved:ym_mk1")) {
                    speedReduction *= 0.95f;
                    hasYieldModule = true;
                } else if (moduleId.equals("agritechevolved:ym_mk2")) {
                    speedReduction *= 0.85f;
                    hasYieldModule = true;
                } else if (moduleId.equals("agritechevolved:ym_mk3")) {
                    speedReduction *= 0.75f;
                    hasYieldModule = true;
                }
            }
        }

        if (hasPowerEfficiencyModule && (hasSpeedModule || hasYieldModule)) {
            return 1.0f;
        }

        return speedModifier * speedReduction;
    }

    private float getModuleYieldModifier() {
        float yieldModifier = 1.0f;
        boolean hasYieldModule = false;
        boolean hasPowerEfficiencyModule = false;

        for (int slot = 2; slot <= 3; slot++) {
            ItemStack moduleStack = inventory.getStackInSlot(slot);
            if (!moduleStack.isEmpty()) {
                String moduleId = RegistryHelper.getItemId(moduleStack);

                if (moduleId.equals("agritechevolved:ym_mk1")) {
                    yieldModifier *= 1.1f;
                    hasYieldModule = true;
                } else if (moduleId.equals("agritechevolved:ym_mk2")) {
                    yieldModifier *= 1.25f;
                    hasYieldModule = true;
                } else if (moduleId.equals("agritechevolved:ym_mk3")) {
                    yieldModifier *= 1.5f;
                    hasYieldModule = true;
                }
                else if (moduleId.startsWith("agritechevolved:pem_mk")) {
                    hasPowerEfficiencyModule = true;
                }
            }
        }

        if (hasPowerEfficiencyModule && hasYieldModule) {
            return 1.0f;
        }

        return yieldModifier;
    }

    private float getModulePowerModifier() {
        float powerModifier = 1.0f;
        boolean hasSpeedModule = false;
        boolean hasPowerEfficiencyModule = false;
        boolean hasYieldModule = false;

        for (int slot = 2; slot <= 3; slot++) {
            ItemStack moduleStack = inventory.getStackInSlot(slot);
            if (!moduleStack.isEmpty()) {
                String moduleId = RegistryHelper.getItemId(moduleStack);

                if (moduleId.equals("agritechevolved:sm_mk1")) {
                    powerModifier *= 1.1f;
                    hasSpeedModule = true;
                } else if (moduleId.equals("agritechevolved:sm_mk2")) {
                    powerModifier *= 1.25f;
                    hasSpeedModule = true;
                } else if (moduleId.equals("agritechevolved:sm_mk3")) {
                    powerModifier *= 1.5f;
                    hasSpeedModule = true;
                }
                else if (moduleId.equals("agritechevolved:pem_mk1")) {
                    powerModifier *= 0.9f;
                    hasPowerEfficiencyModule = true;
                } else if (moduleId.equals("agritechevolved:pem_mk2")) {
                    powerModifier *= 0.75f;
                    hasPowerEfficiencyModule = true;
                } else if (moduleId.equals("agritechevolved:pem_mk3")) {
                    powerModifier *= 0.5f;
                    hasPowerEfficiencyModule = true;
                }
                else if (moduleId.startsWith("agritechevolved:ym_mk")) {
                    hasYieldModule = true;
                }
            }
        }

        if (hasPowerEfficiencyModule && (hasSpeedModule || hasYieldModule)) {
            return 1.0f;
        }

        return powerModifier;
    }

    private float getModuleGrowthModifier() {
        return getModuleSpeedModifier();
    }

    private float getFertilizerGrowthModifier() {
        ItemStack fertilizerStack = inventory.getStackInSlot(4);
        if (fertilizerStack.isEmpty()) {
            return 1.0f;
        }

        String fertilizerId = RegistryHelper.getItemId(fertilizerStack);
        PlantablesConfig.FertilizerInfo fertilizerInfo = PlantablesConfig.getFertilizerInfo(fertilizerId);

        return fertilizerInfo != null ? fertilizerInfo.speedMultiplier : 1.0f;
    }

    private float getFertilizerYieldModifier() {
        ItemStack fertilizerStack = inventory.getStackInSlot(4);
        if (fertilizerStack.isEmpty()) {
            return 1.0f;
        }

        String fertilizerId = RegistryHelper.getItemId(fertilizerStack);
        PlantablesConfig.FertilizerInfo fertilizerInfo = PlantablesConfig.getFertilizerInfo(fertilizerId);

        return fertilizerInfo != null ? fertilizerInfo.yieldMultiplier : 1.0f;
    }

    private int lastGrowthStage = -1;
    private float currentTotalModifier = 1.0f;

    public float getCurrentTotalModifier() {
        ItemStack soilStack = inventory.getStackInSlot(1);
        if (soilStack.isEmpty()) return 1.0f;

        float soilModifier = getSoilGrowthModifier(soilStack);
        float moduleModifier = getModuleGrowthModifier();
        float fertilizerGrowthModifier = getFertilizerGrowthModifier();

        return soilModifier * moduleModifier * fertilizerGrowthModifier;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedPlanterBlockEntity blockEntity) {
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

        float soilModifier = blockEntity.getSoilGrowthModifier(soilStack);
        float moduleModifier = blockEntity.getModuleGrowthModifier();
        float fertilizerGrowthModifier = blockEntity.getFertilizerGrowthModifier();
        float totalModifier = soilModifier * moduleModifier * fertilizerGrowthModifier;

        if (!blockEntity.readyToHarvest) {
            blockEntity.growthTicks++;

            int baseGrowthTime = blockEntity.getBaseGrowthTime(plantStack);
            int adjustedGrowthTime = (int)(baseGrowthTime / totalModifier);

            if (blockEntity.growthTicks >= adjustedGrowthTime) {
                if (!blockEntity.consumeEnergy()) {
                    blockEntity.growthTicks--;
                    return;
                }

                blockEntity.readyToHarvest = true;
                blockEntity.growthProgress = 100;
                blockEntity.lastGrowthStage = blockEntity.getGrowthStage();

                level.sendBlockUpdated(pos, state, state, 3);
                blockEntity.setChanged();
            } else {
                int oldProgress = blockEntity.growthProgress;
                blockEntity.growthProgress = (int)((blockEntity.growthTicks / (float)adjustedGrowthTime) * 100);

                int currentGrowthStage = blockEntity.getGrowthStage();
                if (currentGrowthStage != blockEntity.lastGrowthStage) {
                    if (!blockEntity.consumeEnergy()) {
                        blockEntity.growthProgress = oldProgress;
                        blockEntity.growthTicks--;
                        return;
                    }
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
            return 1200;
        } else if (PlantablesConfig.isValidSapling(itemId)) {
            return PlantablesConfig.getBaseSaplingGrowthTime(itemId);
        }

        return 1200;
    }

    public float getSoilGrowthModifier(ItemStack soilStack) {
        if (soilStack.isEmpty()) return 1.0f;

        String soilId = RegistryHelper.getItemId(soilStack);
        return PlantablesConfig.getSoilGrowthModifier(soilId);
    }

    private static void tryOutputItemsBelow(Level level, BlockPos pos, AdvancedPlanterBlockEntity blockEntity) {
        BlockPos belowPos = pos.below();
        BlockEntity targetEntity = level.getBlockEntity(belowPos);

        if (targetEntity == null) {
            return;
        }

        IItemHandler targetInventory = level.getCapability(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                belowPos,
                Direction.UP);

        if (targetInventory == null) {
            return;
        }

        boolean changed = false;

        for (int slot = 5; slot <= 16; slot++) {
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
        for (int slot = 5; slot <= 16; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            simulatedSlots.put(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }

        for (ItemStack drop : potentialDrops) {
            int remainingToPlace = drop.getCount();

            for (int slot = 5; slot <= 16; slot++) {
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
                for (int slot = 5; slot <= 16; slot++) {
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

        float yieldModifier = getModuleYieldModifier() * getFertilizerYieldModifier();
        drops = applyYieldModifier(drops, yieldModifier);

        for (ItemStack dropStack : drops) {
            int remainingItemsToPlace = dropStack.getCount();

            while (remainingItemsToPlace > 0) {
                boolean itemPlaced = false;

                for (int slot = 5; slot <= 16; slot++) {
                    ItemStack existingStack = inventory.getStackInSlot(slot);

                    if (!existingStack.isEmpty() &&
                            existingStack.is(dropStack.getItem()) &&
                            existingStack.getCount() < existingStack.getMaxStackSize()) {

                        int spaceAvailable = existingStack.getMaxStackSize() - existingStack.getCount();
                        int itemsToAdd = Math.min(spaceAvailable, remainingItemsToPlace);

                        existingStack.grow(itemsToAdd);
                        inventory.setStackInSlot(slot, existingStack);

                        remainingItemsToPlace -= itemsToAdd;
                        itemPlaced = true;

                        if (remainingItemsToPlace <= 0) {
                            break;
                        }
                    }
                }

                if (remainingItemsToPlace > 0) {
                    for (int slot = 5; slot <= 16; slot++) {
                        ItemStack existingStack = inventory.getStackInSlot(slot);

                        if (existingStack.isEmpty()) {
                            ItemStack newStack = new ItemStack(dropStack.getItem(), remainingItemsToPlace);
                            inventory.setStackInSlot(slot, newStack);

                            remainingItemsToPlace = 0;
                            itemPlaced = true;
                            break;
                        }
                    }
                }

                if (!itemPlaced) {
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

    private List<ItemStack> applyYieldModifier(List<ItemStack> drops, float yieldModifier) {
        if (yieldModifier == 1.0f) return drops;

        List<ItemStack> modifiedDrops = new ArrayList<>();
        for (ItemStack drop : drops) {
            int newCount = Math.max(1, (int)(drop.getCount() * yieldModifier));
            modifiedDrops.add(new ItemStack(drop.getItem(), newCount));
        }
        return modifiedDrops;
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
        tag.putInt("energyStored", energyStored);
        tag.putInt("lastGrowthStage", lastGrowthStage);
        tag.putFloat("currentTotalModifier", currentTotalModifier);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        growthProgress = tag.getInt("growthProgress");
        growthTicks = tag.getInt("growthTicks");
        readyToHarvest = tag.getBoolean("readyToHarvest");
        energyStored = tag.getInt("energyStored");
        lastGrowthStage = tag.getInt("lastGrowthStage");
        currentTotalModifier = tag.getFloat("currentTotalModifier");
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
}