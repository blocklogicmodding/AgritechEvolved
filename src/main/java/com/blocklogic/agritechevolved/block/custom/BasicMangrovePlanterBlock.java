package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicMangrovePlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicMangrovePlanterBlock> CODEC = simpleCodec(BasicMangrovePlanterBlock::new);

    public BasicMangrovePlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}