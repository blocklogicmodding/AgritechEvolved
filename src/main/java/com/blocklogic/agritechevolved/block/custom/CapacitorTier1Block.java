package com.blocklogic.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CapacitorTier1Block extends Block {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    public static final MapCodec<CapacitorTier1Block> CODEC = simpleCodec(CapacitorTier1Block::new);

    public CapacitorTier1Block(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
