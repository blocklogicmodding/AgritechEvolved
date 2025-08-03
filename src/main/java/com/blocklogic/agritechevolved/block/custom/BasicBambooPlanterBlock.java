package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicBambooPlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicBambooPlanterBlock> CODEC = simpleCodec(BasicBambooPlanterBlock::new);

    public BasicBambooPlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}