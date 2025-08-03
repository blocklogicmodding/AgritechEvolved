
package com.blocklogic.agritechevolved.block.custom;


import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class BasicJunglePlanterBlock extends BasicPlanterBlock{
    public static final MapCodec<BasicJunglePlanterBlock> CODEC = simpleCodec(BasicJunglePlanterBlock::new);

    public BasicJunglePlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}