package com.blocklogic.agritechevolved.util;

import com.blocklogic.agritechevolved.AgritechEvolved;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ATETags {
    public static class Blocks {
        public static final TagKey<Block> PLANTERS = createTag("planters");
        public static final TagKey<Block> BASIC_PLANTERS = createTag("basic_planters");
        public static final TagKey<Block> MACHINES = createTag("machines");
        public static final TagKey<Block> NATURAL_BLOCKS = createTag("natural_blocks");
        public static final TagKey<Block> DIRT_LIKE_BLOCKS = createTag("dirt_like_blocks");

        private static TagKey<Block> createTag (String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ATE_MODULES = createTag("agritechevolved_modules");
        public static final TagKey<Item> BIOMASS = createTag("biomass");
        public static final TagKey<Item> DIRT_LIKE_BLOCK_ITEMS = createTag("dirt_like_block_items");
        public static final TagKey<Item> BASIC_PLANTER_ITEMS = createTag("basic_planter_items");


        private static TagKey<Item> createTag (String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, name));
        }
    }
}
