package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicCrimsonPlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicCrimsonPlanterBlock> CODEC = simpleCodec(BasicCrimsonPlanterBlock::new);

    public BasicCrimsonPlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}