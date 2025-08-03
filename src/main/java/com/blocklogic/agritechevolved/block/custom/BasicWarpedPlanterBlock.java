package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicWarpedPlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicWarpedPlanterBlock> CODEC = simpleCodec(BasicWarpedPlanterBlock::new);

    public BasicWarpedPlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}