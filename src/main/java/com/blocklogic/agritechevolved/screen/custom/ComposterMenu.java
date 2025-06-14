package com.blocklogic.agritechevolved.screen.custom;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.block.entity.ComposterBlockEntity;
import com.blocklogic.agritechevolved.config.CompostableConfig;
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

public class ComposterMenu extends AbstractContainerMenu {
    public final ComposterBlockEntity blockEntity;
    private final Level level;
    private int lastEnergyStored = 0;
    private int lastProgress = 0;

    // Slot constants
    private static final int INPUT_SLOTS_START = 0;
    private static final int INPUT_SLOTS_COUNT = 12;
    private static final int OUTPUT_SLOTS_START = 12;
    private static final int OUTPUT_SLOTS_COUNT = 3;
    private static final int MODULE_SLOT = 15;

    public ComposterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ComposterMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.COMPOSTER_MENU.get(), containerId);
        this.blockEntity = ((ComposterBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Add input slots (12 slots, 3x4 grid) - Accept only compostable items
        int inputSlotIndex = INPUT_SLOTS_START;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                this.addSlot(new CompostableInputSlot(this.blockEntity.inventory, inputSlotIndex++,
                        8 + col * 18, 15 + row * 18));
            }
        }

        // Add output slots (3 slots, vertical) - Extract only
        int outputSlotIndex = OUTPUT_SLOTS_START;
        for (int row = 0; row < 3; row++) {
            this.addSlot(new OutputOnlySlot(this.blockEntity.inventory, outputSlotIndex++,
                    98, 15 + row * 18));
        }

        // Add module slot - Accept only speed modules
        this.addSlot(new ModuleSlot(this.blockEntity.inventory, MODULE_SLOT, 134, 15));

        addDataSlots();
    }

    // Custom slot for compostable input items only
    private static class CompostableInputSlot extends SlotItemHandler {
        public CompostableInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.isEmpty()) return false;
            String itemId = RegistryHelper.getItemId(stack);
            return CompostableConfig.isCompostable(itemId);
        }
    }

    // Custom slot for output only (no insertion allowed)
    private static class OutputOnlySlot extends SlotItemHandler {
        public OutputOnlySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false; // Never allow insertion
        }
    }

    // Custom slot for speed modules only
    private static class ModuleSlot extends SlotItemHandler {
        public ModuleSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.isEmpty()) return false;
            String itemId = RegistryHelper.getItemId(stack);
            return itemId.equals("agritechevolved:sm_mk1") ||
                    itemId.equals("agritechevolved:sm_mk2") ||
                    itemId.equals("agritechevolved:sm_mk3");
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
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
                return blockEntity.getProgress();
            }

            @Override
            public void set(int value) {
                lastProgress = value;
            }
        });
    }

    // GUI data getters
    public int getEnergyStored() {
        return level.isClientSide ? lastEnergyStored : blockEntity.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return blockEntity.getMaxEnergyStored();
    }

    public int getProgress() {
        return level.isClientSide ? lastProgress : blockEntity.getProgress();
    }

    public int getMaxProgress() {
        return blockEntity.getMaxProgress();
    }

    public int getOrganicItemsCollected() {
        return blockEntity.getOrganicItemsCollected();
    }

    public int getRequiredOrganicItems() {
        return blockEntity.getRequiredOrganicItems();
    }

    // Shift-click handling
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 16;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // If shift-clicking from player inventory
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            String sourceItemId = RegistryHelper.getItemId(sourceStack);

            // Try to place compostable items in input slots
            if (CompostableConfig.isCompostable(sourceItemId)) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + INPUT_SLOTS_START,
                        TE_INVENTORY_FIRST_SLOT_INDEX + INPUT_SLOTS_START + INPUT_SLOTS_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // Try to place speed modules in module slot
            else if (sourceItemId.equals("agritechevolved:sm_mk1") ||
                    sourceItemId.equals("agritechevolved:sm_mk2") ||
                    sourceItemId.equals("agritechevolved:sm_mk3")) {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + MODULE_SLOT,
                        TE_INVENTORY_FIRST_SLOT_INDEX + MODULE_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // If item doesn't fit anywhere specific, try general container slots
            else {
                if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                        + TE_INVENTORY_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        // If shift-clicking from container inventory (only output slots should be extractable)
        else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // Move to player inventory
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
                player, ATEBlocks.COMPOSTER.get());
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