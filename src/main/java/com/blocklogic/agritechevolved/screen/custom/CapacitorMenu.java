package com.blocklogic.agritechevolved.screen.custom;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.block.entity.CapacitorBlockEntity;
import com.blocklogic.agritechevolved.screen.ATEMenuTypes;
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

public class CapacitorMenu extends AbstractContainerMenu {
    public final CapacitorBlockEntity blockEntity;
    private final Level level;

    public CapacitorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CapacitorMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ATEMenuTypes.CAPACITOR_MENU.get(), containerId);
        this.blockEntity = ((CapacitorBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots();
    }

    private void addDataSlots() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getEnergyStorage().getEnergyStored() & 0xFFFF;
            }

            @Override
            public void set(int value) {
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (blockEntity.getEnergyStorage().getEnergyStored() >> 16) & 0xFFFF;
            }

            @Override
            public void set(int value) {
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getEnergyStorage().getMaxEnergyStored() & 0xFFFF;
            }

            @Override
            public void set(int value) {
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (blockEntity.getEnergyStorage().getMaxEnergyStored() >> 16) & 0xFFFF;
            }

            @Override
            public void set(int value) {
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getTransferRate();
            }

            @Override
            public void set(int value) {
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getTier();
            }

            @Override
            public void set(int value) {
            }
        });
    }

    public int getEnergyStored() {
        return blockEntity.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return blockEntity.getMaxEnergyStored();
    }

    public int getTransferRate() {
        return blockEntity.getTransferRate();
    }

    public int getTier() {
        return blockEntity.getTier();
    }

    public String getTierName() {
        int tier = getTier();
        return switch (tier) {
            case 1 -> "Tier 1";
            case 2 -> "Tier 2";
            case 3 -> "Tier 3";
            default -> "Unknown";
        };
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ATEBlocks.CAPACITOR_TIER1.get()) ||
                stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                        player, ATEBlocks.CAPACITOR_TIER2.get()) ||
                stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                        player, ATEBlocks.CAPACITOR_TIER3.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 66 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 125));
        }
    }
}