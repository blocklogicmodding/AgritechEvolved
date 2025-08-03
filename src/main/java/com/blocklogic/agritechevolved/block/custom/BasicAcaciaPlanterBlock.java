package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BasicAcaciaPlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicAcaciaPlanterBlock> CODEC = simpleCodec(BasicAcaciaPlanterBlock::new);

    public BasicAcaciaPlanterBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}