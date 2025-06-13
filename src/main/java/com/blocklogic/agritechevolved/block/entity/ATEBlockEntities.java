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

    public static final Supplier<BlockEntityType<BiomassBurnerBlockEntity>> BURNER_BE =
            BLOCK_ENTITIES.register("burner_be", () -> BlockEntityType.Builder.of(
                    BiomassBurnerBlockEntity::new, ATEBlocks.BIOMASS_BURNER.get()).build(null));

    public static final Supplier<BlockEntityType<ComposterBlockEntity>> COMPOSTER_BE =
            BLOCK_ENTITIES.register("composter_be", () -> BlockEntityType.Builder.of(
                    ComposterBlockEntity::new, ATEBlocks.COMPOSTER.get()).build(null));

    public static final Supplier<BlockEntityType<InfuserBlockEntity>> INFUSER_BE =
            BLOCK_ENTITIES.register("infuser_be", () -> BlockEntityType.Builder.of(
                    InfuserBlockEntity::new, ATEBlocks.INFUSER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
