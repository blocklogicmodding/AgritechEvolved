package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ATELootTableProvider extends BlockLootSubProvider {
    protected ATELootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ATEBlocks.BASIC_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_ACACIA_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_BAMBOO_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_BIRCH_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_CHERRY_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_CRIMSON_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_DARK_OAK_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_JUNGLE_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_MANGROVE_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_SPRUCE_PLANTER.get());
        dropSelf(ATEBlocks.BASIC_WARPED_PLANTER.get());
        dropSelf(ATEBlocks.ADVANCED_PLANTER.get());
        dropSelf(ATEBlocks.COMPOSTER.get());
        dropSelf(ATEBlocks.BIOMASS_BURNER.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER1.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER2.get());
        dropSelf(ATEBlocks.CAPACITOR_TIER3.get());
        dropSelf(ATEBlocks.INFUSED_FARMLAND.get());
        dropSelf(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get());
        dropSelf(ATEBlocks.MULCH.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ATEBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
