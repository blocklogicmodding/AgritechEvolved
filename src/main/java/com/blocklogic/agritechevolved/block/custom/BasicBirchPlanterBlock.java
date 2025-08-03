package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicBirchPlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicBirchPlanterBlock> CODEC = simpleCodec(BasicBirchPlanterBlock::new);

    public BasicBirchPlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}