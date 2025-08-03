package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicSprucePlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicSprucePlanterBlock> CODEC = simpleCodec(BasicSprucePlanterBlock::new);

    public BasicSprucePlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}