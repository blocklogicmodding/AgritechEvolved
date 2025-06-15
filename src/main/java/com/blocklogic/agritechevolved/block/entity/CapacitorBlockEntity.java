package com.blocklogic.agritechevolved.block.entity;

import com.blocklogic.agritechevolved.Config;
import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.block.custom.CapacitorTier1Block;
import com.blocklogic.agritechevolved.block.custom.CapacitorTier2Block;
import com.blocklogic.agritechevolved.block.custom.CapacitorTier3Block;
import com.blocklogic.agritechevolved.screen.custom.CapacitorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class CapacitorBlockEntity extends BlockEntity implements MenuProvider {

    private EnergyStorage energyStorage;
    private int tier = 1;
    private int transferRate = 512;

    public CapacitorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ATEBlockEntities.CAPACITOR_BE.get(), pos, blockState);
        initializeCapacitor(blockState);
    }

    private void initializeCapacitor(BlockState blockState) {
        if (blockState.is(ATEBlocks.CAPACITOR_TIER1.get())) {
            tier = 1;
            transferRate = Config.getCapacitorT1TransferRate();
            energyStorage = new EnergyStorage(Config.getCapacitorT1Buffer()) {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    int received = super.receiveEnergy(Math.min(maxReceive, transferRate), simulate);
                    if (!simulate && received > 0) {
                        setChanged();
                        if (!level.isClientSide()) {
                            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                        }
                    }
                    return received;
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    int extracted = super.extractEnergy(Math.min(maxExtract, transferRate), simulate);
                    if (!simulate && extracted > 0) {
                        setChanged();
                        if (!level.isClientSide()) {
                            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                        }
                    }
                    return extracted;
                }
            };
        } else if (blockState.is(ATEBlocks.CAPACITOR_TIER2.get())) {
            tier = 2;
            transferRate = Config.getCapacitorT2TransferRate();
            energyStorage = new EnergyStorage(Config.getCapacitorT2Buffer()) {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    int received = super.receiveEnergy(Math.min(maxReceive, transferRate), simulate);
                    if (!simulate && received > 0) {
                        setChanged();
                        if (!level.isClientSide()) {
                            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                        }
                    }
                    return received;
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    int extracted = super.extractEnergy(Math.min(maxExtract, transferRate), simulate);
                    if (!simulate && extracted > 0) {
                        setChanged();
                        if (!level.isClientSide()) {
                            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                        }
                    }
                    return extracted;
                }
            };
        } else if (blockState.is(ATEBlocks.CAPACITOR_TIER3.get())) {
            tier = 3;
            transferRate = Config.getCapacitorT3TransferRate();
            energyStorage = new EnergyStorage(Config.getCapacitorT3Buffer()) {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    int received = super.receiveEnergy(Math.min(maxReceive, transferRate), simulate);
                    if (!simulate && received > 0) {
                        setChanged();
                        if (!level.isClientSide()) {
                            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                        }
                    }
                    return received;
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    int extracted = super.extractEnergy(Math.min(maxExtract, transferRate), simulate);
                    if (!simulate && extracted > 0) {
                        setChanged();
                        if (!level.isClientSide()) {
                            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                        }
                    }
                    return extracted;
                }
            };
        } else {
            tier = 1;
            transferRate = Config.getCapacitorT1TransferRate();
            energyStorage = new EnergyStorage(Config.getCapacitorT1Buffer());
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CapacitorBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) return;

        boolean wasChanged = false;

        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) continue;

            if (blockEntity.energyStorage.getEnergyStored() <= 0) break;

            BlockPos neighborPos = pos.relative(direction);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            if (neighborBE != null) {
                IEnergyStorage neighborEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite());
                if (neighborEnergy != null && neighborEnergy.canReceive()) {
                    int energyToTransfer = blockEntity.energyStorage.extractEnergy(blockEntity.transferRate, true);
                    if (energyToTransfer > 0) {
                        int transferred = neighborEnergy.receiveEnergy(energyToTransfer, false);
                        if (transferred > 0) {
                            blockEntity.energyStorage.extractEnergy(transferred, false);
                            wasChanged = true;
                        }
                    }
                }
            }
        }

        if (wasChanged) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        boolean hasEnergy = blockEntity.getEnergyStored() > 0;

        if (state.is(ATEBlocks.CAPACITOR_TIER1.get())) {
            boolean currentlyHasEnergy = state.getValue(CapacitorTier1Block.HAS_ENERGY);
            if (hasEnergy != currentlyHasEnergy) {
                level.setBlock(pos, state.setValue(CapacitorTier1Block.HAS_ENERGY, hasEnergy), 3);
            }
        } else if (state.is(ATEBlocks.CAPACITOR_TIER2.get())) {
            boolean currentlyHasEnergy = state.getValue(CapacitorTier2Block.HAS_ENERGY);
            if (hasEnergy != currentlyHasEnergy) {
                level.setBlock(pos, state.setValue(CapacitorTier2Block.HAS_ENERGY, hasEnergy), 3);
            }
        } else if (state.is(ATEBlocks.CAPACITOR_TIER3.get())) {
            boolean currentlyHasEnergy = state.getValue(CapacitorTier3Block.HAS_ENERGY);
            if (hasEnergy != currentlyHasEnergy) {
                level.setBlock(pos, state.setValue(CapacitorTier3Block.HAS_ENERGY, hasEnergy), 3);
            }
        }
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getTier() {
        return tier;
    }

    public int getTransferRate() {
        return transferRate;
    }

    public String getTierName() {
        return switch (tier) {
            case 1 -> "Tier 1";
            case 2 -> "Tier 2";
            case 3 -> "Tier 3";
            default -> "Unknown";
        };
    }

    public void forceSetEnergy(int energy) {
        if (energyStorage != null) {
            int maxEnergy = energyStorage.getMaxEnergyStored();
            int clampedEnergy = Math.min(energy, maxEnergy);

            if (energyStorage instanceof EnergyStorage storage) {
                try {
                    var energyField = EnergyStorage.class.getDeclaredField("energy");
                    energyField.setAccessible(true);
                    energyField.setInt(storage, clampedEnergy);
                } catch (Exception e) {

                    while (energyStorage.getEnergyStored() < clampedEnergy &&
                            energyStorage.receiveEnergy(clampedEnergy - energyStorage.getEnergyStored(), false) > 0) {
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (energyStorage != null) {
            tag.put("energy", energyStorage.serializeNBT(registries));
        }
        tag.putInt("tier", tier);
        tag.putInt("transferRate", transferRate);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (getBlockState() != null) {
            initializeCapacitor(getBlockState());
        }

        if (tag.contains("energy") && energyStorage != null) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
        if (tag.contains("tier")) {
            tier = tag.getInt("tier");
        }
        if (tag.contains("transferRate")) {
            transferRate = tag.getInt("transferRate");
        }
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

    public int getEnergyStored() {
        return energyStorage != null ? energyStorage.getEnergyStored() : 0;
    }

    public int getMaxEnergyStored() {
        return energyStorage != null ? energyStorage.getMaxEnergyStored() : 0;
    }

    public boolean canExtractEnergy() {
        return true;
    }

    public boolean canReceiveEnergy() {
        return true;
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyStorage != null ? energyStorage.receiveEnergy(maxReceive, simulate) : 0;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage != null ? energyStorage.extractEnergy(maxExtract, simulate) : 0;
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        if (side == Direction.DOWN) return null;

        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return CapacitorBlockEntity.this.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return CapacitorBlockEntity.this.extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return CapacitorBlockEntity.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return CapacitorBlockEntity.this.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return CapacitorBlockEntity.this.canExtractEnergy();
            }

            @Override
            public boolean canReceive() {
                return CapacitorBlockEntity.this.canReceiveEnergy();
            }
        };
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ATEBlockEntities.CAPACITOR_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof CapacitorBlockEntity capacitorBlockEntity) {
                        return capacitorBlockEntity.getEnergyStorage(direction);
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
        return Component.translatable("gui.agritechevolved.capacitor_tier" + tier);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CapacitorMenu(containerId, playerInventory, this);
    }
}