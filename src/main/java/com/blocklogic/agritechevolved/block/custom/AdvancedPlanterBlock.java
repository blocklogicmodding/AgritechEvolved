package com.blocklogic.agritechevolved.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AdvancedPlanterBlock extends Block {
    public static final VoxelShape SHAPE = Block.box(1.0, 0, 1.0, 15, 12, 15);
    public static final MapCodec<AdvancedPlanterBlock> CODEC = simpleCodec(AdvancedPlanterBlock::new);

    public AdvancedPlanterBlock(Properties properties) {
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
