package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicDarkOakPlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicDarkOakPlanterBlock> CODEC = simpleCodec(BasicDarkOakPlanterBlock::new);

    public BasicDarkOakPlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}