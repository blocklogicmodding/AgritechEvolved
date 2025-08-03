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

    public static final Supplier<BlockEntityType<BasicPlanterBlockEntity>> BASIC_PLANTER_BE =
            BLOCK_ENTITIES.register("basic_planter_be", () -> BlockEntityType.Builder.of(
                    BasicPlanterBlockEntity::new,
                    ATEBlocks.BASIC_PLANTER.get(),
                    ATEBlocks.BASIC_ACACIA_PLANTER.get(),
                    ATEBlocks.BASIC_BAMBOO_PLANTER.get(),
                    ATEBlocks.BASIC_BIRCH_PLANTER.get(),
                    ATEBlocks.BASIC_CHERRY_PLANTER.get(),
                    ATEBlocks.BASIC_CRIMSON_PLANTER.get(),
                    ATEBlocks.BASIC_DARK_OAK_PLANTER.get(),
                    ATEBlocks.BASIC_JUNGLE_PLANTER.get(),
                    ATEBlocks.BASIC_MANGROVE_PLANTER.get(),
                    ATEBlocks.BASIC_SPRUCE_PLANTER.get(),
                    ATEBlocks.BASIC_WARPED_PLANTER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<AdvancedPlanterBlockEntity>> ADVANCED_PLANTER_BE =
            BLOCK_ENTITIES.register("advanced_planter_be", () -> BlockEntityType.Builder.of(
                    AdvancedPlanterBlockEntity::new, ATEBlocks.ADVANCED_PLANTER.get()).build(null));

    public static final Supplier<BlockEntityType<BiomassBurnerBlockEntity>> BURNER_BE =
            BLOCK_ENTITIES.register("burner_be", () -> BlockEntityType.Builder.of(
                    BiomassBurnerBlockEntity::new, ATEBlocks.BIOMASS_BURNER.get()).build(null));

    public static final Supplier<BlockEntityType<ComposterBlockEntity>> COMPOSTER_BE =
            BLOCK_ENTITIES.register("composter_be", () -> BlockEntityType.Builder.of(
                    ComposterBlockEntity::new, ATEBlocks.COMPOSTER.get()).build(null));

    public static final Supplier<BlockEntityType<CapacitorBlockEntity>> CAPACITOR_BE =
            BLOCK_ENTITIES.register("capacitor_be", () -> BlockEntityType.Builder.of(
                    CapacitorBlockEntity::new,
                    ATEBlocks.CAPACITOR_TIER1.get(),
                    ATEBlocks.CAPACITOR_TIER2.get(),
                    ATEBlocks.CAPACITOR_TIER3.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
