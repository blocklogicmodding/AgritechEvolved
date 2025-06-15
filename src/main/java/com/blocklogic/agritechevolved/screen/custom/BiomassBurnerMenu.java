package com.blocklogic.agritechevolved.screen.custom;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.block.entity.BiomassBurnerBlockEntity;
import com.blocklogic.agritechevolved.screen.ATEMenuTypes;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BiomassBurnerMenu extends AbstractContainerMenu {
    public final BiomassBurnerBlockEntity blockEntity;
    private final Level level;
    private int lastEnergyStored = 0;
    private int lastProgress = 0;

    // Slot constants
    private static final int FUEL_SLOT = 0;

    public BiomassBurnerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BiomassBurnerMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.BURNER_MENU.get(), containerId);
        this.blockEntity = ((BiomassBurnerBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new BiomassInputSlot(this.blockEntity.inventory, FUEL_SLOT, 80, 32));

        addDataSlots();
    }

    private static class BiomassInputSlot extends SlotItemHandler {
        public BiomassInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.isEmpty()) return false;
            String itemId = RegistryHelper.getItemId(stack);
            return itemId.equals("agritechevolved:biomass") ||
                    itemId.equals("agritechevolved:compacted_biomass");
        }
    }

    private void addDataSlots() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getEnergyStorage().getEnergyStored();
            }

            @Override
            public void set(int value) {
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getProgress();
            }

            @Override
            public void set(int value) {
                blockEntity.setProgress(value);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getMaxProgress();
            }

            @Override
            public void set(int value) {
            }
        });
    }

    public int getEnergyStored() {
        return this.blockEntity.getEnergyStorage().getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return this.blockEntity.getEnergyStorage().getMaxEnergyStored();
    }

    public int getProgress() {
        return this.blockEntity.getProgress();
    }

    public int getMaxProgress() {
        return this.blockEntity.getMaxProgress();
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 1;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            String sourceItemId = RegistryHelper.getItemId(sourceStack);

            if (sourceItemId.equals("agritechevolved:biomass") ||
                    sourceItemId.equals("agritechevolved:compacted_biomass")) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + FUEL_SLOT,
                        TE_INVENTORY_FIRST_SLOT_INDEX + FUEL_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                        + TE_INVENTORY_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
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
                player, ATEBlocks.BIOMASS_BURNER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 81 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 140));
        }
    }
}