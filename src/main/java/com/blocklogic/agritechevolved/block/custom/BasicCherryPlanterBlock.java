package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicCherryPlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicCherryPlanterBlock> CODEC = simpleCodec(BasicCherryPlanterBlock::new);

    public BasicCherryPlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}