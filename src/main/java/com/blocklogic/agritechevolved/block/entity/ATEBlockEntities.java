package com.blocklogic.agritechevolved.block.entity;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.ATEBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ATEBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AgritechEvolved.MODID);

    public static final Supplier<BlockEntityType<AdvancedPlanterBlockEntity>> PLANTER_BE =
            BLOCK_ENTITIES.register("planter_be", () -> BlockEntityType.Builder.of(
                    AdvancedPlanterBlockEntity::new, ATEBlocks.ADVANCED_PLANTER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
