package com.blocklogic.agritechevolved.block.custom;

import com.blocklogic.agritechevolved.block.entity.ATEBlockEntities;
import com.blocklogic.agritechevolved.block.entity.CapacitorBlockEntity;
import com.blocklogic.agritechevolved.component.ATEDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CapacitorTier1Block extends BaseEntityBlock {
    public static final MapCodec<CapacitorTier1Block> CODEC = simpleCodec(CapacitorTier1Block::new);
    public static final BooleanProperty HAS_ENERGY = BooleanProperty.create("has_energy");

    public CapacitorTier1Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_ENERGY, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_ENERGY);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CapacitorBlockEntity(blockPos, blockState);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CapacitorBlockEntity capacitorBlockEntity) {
            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(capacitorBlockEntity, Component.translatable("gui.agritechevolved.capacitor")), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CapacitorBlockEntity capacitorBlockEntity) {
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);

        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof CapacitorBlockEntity capacitorBlockEntity && !drops.isEmpty()) {
            ItemStack drop = drops.get(0);

            int storedEnergy = capacitorBlockEntity.getEnergyStored();
            if (storedEnergy > 0) {
                drop.set(ATEDataComponents.STORED_ENERGY.get(), storedEnergy);
                drop.set(ATEDataComponents.CAPACITOR_TIER.get(), capacitorBlockEntity.getTier());
            }
        }

        return drops;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        Integer storedEnergy = stack.get(ATEDataComponents.STORED_ENERGY.get());
        if (storedEnergy != null && storedEnergy > 0) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CapacitorBlockEntity capacitorBlockEntity) {
                capacitorBlockEntity.forceSetEnergy(storedEnergy);
                capacitorBlockEntity.setChanged();
            }
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ATEBlockEntities.CAPACITOR_BE.get() ?
                (lvl, pos, blockState, blockEntity) -> CapacitorBlockEntity.tick(lvl, pos, blockState, (CapacitorBlockEntity)blockEntity) :
                null;
    }
}