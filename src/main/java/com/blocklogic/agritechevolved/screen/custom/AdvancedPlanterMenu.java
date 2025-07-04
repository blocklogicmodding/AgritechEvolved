package com.blocklogic.agritechevolved.screen.custom;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.block.entity.AdvancedPlanterBlockEntity;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.screen.ATEMenuTypes;
import com.blocklogic.agritechevolved.util.ATETags;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AdvancedPlanterMenu extends AbstractContainerMenu {
    public final AdvancedPlanterBlockEntity blockEntity;
    private final Level level;
    private int lastEnergyStored = 0;
    private int lastGrowthProgress = 0;

    public AdvancedPlanterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public AdvancedPlanterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.ADVANCED_PLANTER_MENU.get(), containerId);
        this.blockEntity = ((AdvancedPlanterBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 8, 16));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 1, 8, 52));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 2, 152, 16));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 3, 170, 16));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 4, 161, 52));

        int outputSlotIndex = 5;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                this.addSlot(new SlotItemHandler(this.blockEntity.inventory, outputSlotIndex++,
                        62 + col * 18, 16 + row * 18));
            }
        }

        addDataSlots();
    }

    private void addDataSlots() {
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getEnergyStored();
            }

            @Override
            public void set(int value) {
                lastEnergyStored = value;
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return Math.round(blockEntity.getGrowthProgress() * 1000);
            }

            @Override
            public void set(int value) {
                lastGrowthProgress = value;
            }
        });
    }

    public int getEnergyStored() {
        return level.isClientSide ? lastEnergyStored : blockEntity.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return blockEntity.getMaxEnergyStored();
    }

    public float getGrowthProgress() {
        return level.isClientSide ? (lastGrowthProgress / 1000.0f) : blockEntity.getGrowthProgress();
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 17;
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            String sourceItemId = RegistryHelper.getItemId(sourceStack);

            if (PlantablesConfig.isValidSeed(sourceItemId) || PlantablesConfig.isValidSapling(sourceItemId)) {
                if (this.blockEntity.inventory.getStackInSlot(0).isEmpty()) {
                    ItemStack existingSoil = this.blockEntity.inventory.getStackInSlot(1);
                    if (!existingSoil.isEmpty()) {
                        String soilId = RegistryHelper.getItemId(existingSoil);
                        boolean validCombination = false;

                        if (PlantablesConfig.isValidSeed(sourceItemId)) {
                            validCombination = PlantablesConfig.isSoilValidForSeed(soilId, sourceItemId);
                        } else if (PlantablesConfig.isValidSapling(sourceItemId)) {
                            validCombination = PlantablesConfig.isSoilValidForSapling(soilId, sourceItemId);
                        }

                        if (!validCombination) {
                            return ItemStack.EMPTY;
                        }
                    }

                    ItemStack plantStack = sourceStack.copyWithCount(1);
                    this.blockEntity.inventory.setStackInSlot(0, plantStack);
                    sourceStack.shrink(1);
                    return copyOfSourceStack;
                }
            }

            else if (PlantablesConfig.isValidSoil(sourceItemId)) {
                if (this.blockEntity.inventory.getStackInSlot(1).isEmpty()) {
                    ItemStack existingPlant = this.blockEntity.inventory.getStackInSlot(0);
                    if (!existingPlant.isEmpty()) {
                        String plantId = RegistryHelper.getItemId(existingPlant);
                        boolean validCombination = false;

                        if (PlantablesConfig.isValidSeed(plantId)) {
                            validCombination = PlantablesConfig.isSoilValidForSeed(sourceItemId, plantId);
                        } else if (PlantablesConfig.isValidSapling(plantId)) {
                            validCombination = PlantablesConfig.isSoilValidForSapling(sourceItemId, plantId);
                        }

                        if (!validCombination) {
                            return ItemStack.EMPTY;
                        }
                    }

                    ItemStack soilStack = sourceStack.copyWithCount(1);
                    this.blockEntity.inventory.setStackInSlot(1, soilStack);
                    sourceStack.shrink(1);
                    return copyOfSourceStack;
                }
            }

            else if (PlantablesConfig.isValidFertilizer(sourceItemId)) {
                if (this.blockEntity.inventory.getStackInSlot(4).isEmpty()) {
                    ItemStack fertilizerStack = sourceStack.copyWithCount(1);
                    this.blockEntity.inventory.setStackInSlot(4, fertilizerStack);
                    sourceStack.shrink(1);
                    return copyOfSourceStack;
                }
            }

            else if (sourceStack.is(ATETags.Items.ATE_MODULES)) {
                for (int slot = 2; slot <= 3; slot++) {
                    if (this.blockEntity.inventory.getStackInSlot(slot).isEmpty()) {
                        ItemStack moduleStack = sourceStack.copyWithCount(1);
                        this.blockEntity.inventory.setStackInSlot(slot, moduleStack);
                        sourceStack.shrink(1);
                        return copyOfSourceStack;
                    }
                }
            }

            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ATEBlocks.ADVANCED_PLANTER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 26 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 26 + i * 18, 144));
        }
    }
}