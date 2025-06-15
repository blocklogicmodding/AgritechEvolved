package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.item.ATEItems;
import com.blocklogic.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ATERecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ATERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        buildVanillaRecipes(recipeOutput);
        buildDurabilityRecipes(recipeOutput);
    }

    protected void buildVanillaRecipes(RecipeOutput recipeOutput) {
        // Basic Planter
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BASIC_PLANTER.get())
                .pattern("S S")
                .pattern("PDP")
                .pattern("LHL")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('L', ItemTags.LOGS)
                .define('D', Items.DIRT)
                .define('H', Items.HOPPER)
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        // Advanced Planter
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.ADVANCED_PLANTER.get())
                .pattern("F F")
                .pattern("IAI")
                .pattern("RIR")
                .define('F', Items.IRON_INGOT)
                .define('I', Items.IRON_BLOCK)
                .define('A', ATEBlocks.BASIC_PLANTER)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_basic_planter", has(ATEBlocks.BASIC_PLANTER))
                .save(recipeOutput);

        // Composter
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.COMPOSTER.get())
                .pattern("I I")
                .pattern("ICI")
                .pattern("IRI")
                .define('C', Items.COMPOSTER)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_composter", has(Items.COMPOSTER))
                .save(recipeOutput);

        // Biomass Burner
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.BIOMASS_BURNER.get())
                .pattern("III")
                .pattern("IFI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('F', Items.FURNACE)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(recipeOutput);

        // Capacitor Tier 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER1.get())
                .pattern("RRR")
                .pattern("ICI")
                .pattern("RRR")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_BLOCK)
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(recipeOutput);

        // Capacitor Tier 2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER2.get())
                .pattern("RRR")
                .pattern("GCG")
                .pattern("RRR")
                .define('R', Items.REDSTONE_BLOCK)
                .define('G', Items.GOLD_INGOT)
                .define('C', ATEBlocks.CAPACITOR_TIER1.get())
                .unlockedBy("has_capacitor_tier1", has(ATEBlocks.CAPACITOR_TIER1.get()))
                .save(recipeOutput);

        // Capacitor Tier 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEBlocks.CAPACITOR_TIER3.get())
                .pattern("DDD")
                .pattern("ECE")
                .pattern("DDD")
                .define('D', Items.DIAMOND)
                .define('E', Items.EMERALD)
                .define('C', ATEBlocks.CAPACITOR_TIER2.get())
                .unlockedBy("has_capacitor_tier2", has(ATEBlocks.CAPACITOR_TIER2.get()))
                .save(recipeOutput);

        // Speed Modules
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.SM_MK1.get())
                .pattern(" R ")
                .pattern("IGI")
                .pattern(" R ")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.SM_MK2.get())
                .pattern(" D ")
                .pattern("GSG")
                .pattern(" D ")
                .define('D', Items.DIAMOND)
                .define('G', Items.GOLD_BLOCK)
                .define('S', ATEItems.SM_MK1.get())
                .unlockedBy("has_sm_mk1", has(ATEItems.SM_MK1.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.SM_MK3.get())
                .pattern("ENE")
                .pattern("DSD")
                .pattern("ENE")
                .define('N', Items.NETHERITE_INGOT)
                .define('D', Items.DIAMOND_BLOCK)
                .define('E', Items.EMERALD_BLOCK)
                .define('S', ATEItems.SM_MK2.get())
                .unlockedBy("has_sm_mk2", has(ATEItems.SM_MK2.get()))
                .save(recipeOutput);

        // Yield Modules
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.YM_MK1.get())
                .pattern(" W ")
                .pattern("SCS")
                .pattern(" W ")
                .define('W', Items.WHEAT)
                .define('S', Items.WHEAT_SEEDS)
                .define('C', Items.COPPER_BLOCK)
                .unlockedBy("has_farmland", has(Items.FARMLAND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.YM_MK2.get())
                .pattern(" G ")
                .pattern("CYC")
                .pattern(" G ")
                .define('G', Items.GOLD_BLOCK)
                .define('C', Items.COPPER_BLOCK)
                .define('Y', ATEItems.YM_MK1.get())
                .unlockedBy("has_ym_mk1", has(ATEItems.YM_MK1.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.YM_MK3.get())
                .pattern("ENE")
                .pattern("GYG")
                .pattern("ENE")
                .define('E', Items.ENCHANTED_GOLDEN_APPLE)
                .define('G', Items.GOLD_BLOCK)
                .define('N', Items.NETHERITE_INGOT)
                .define('Y', ATEItems.YM_MK2.get())
                .unlockedBy("has_ym_mk2", has(ATEItems.YM_MK2.get()))
                .save(recipeOutput);

        // Compacted Biomass - 3x3 biomass
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.COMPACTED_BIOMASS.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ATEItems.BIOMASS.get())
                .unlockedBy("has_biomass", has(ATEItems.BIOMASS.get()))
                .save(recipeOutput);

        // Compacted Biomass back to biomass
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEItems.BIOMASS.get(), 9)
                .requires(ATEItems.COMPACTED_BIOMASS.get())
                .unlockedBy("has_compacted_biomass", has(ATEItems.COMPACTED_BIOMASS.get()))
                .save(recipeOutput, "biomass_from_compacted");

        // Compacted Biomass Block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ATEBlocks.COMPACTED_BIOMASS_BLOCK.get())
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', ATEItems.COMPACTED_BIOMASS.get())
                .unlockedBy("has_compacted_biomass", has(ATEItems.COMPACTED_BIOMASS.get()))
                .save(recipeOutput);

        // Compacted Biomass Block back to items
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ATEItems.COMPACTED_BIOMASS.get(), 9)
                .requires(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get())
                .unlockedBy("has_compacted_biomass_block", has(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get()))
                .save(recipeOutput, "compacted_biomass_from_block");

        // Mulch - requires infused farmland + biomass
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ATEBlocks.MULCH.get(), 8)
                .pattern("BBB")
                .pattern("BIB")
                .pattern("BBB")
                .define('B', ATEItems.COMPACTED_BIOMASS.get())
                .define('I', Items.FARMLAND)
                .unlockedBy("has_infused_farmland", has(ATEBlocks.INFUSED_FARMLAND.get()))
                .save(recipeOutput);

        // Crude Biomass
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ATEItems.CRUDE_BIOMASS)
                .pattern("LLL")
                .pattern("DDD")
                .pattern("LLL")
                .define('L', ItemTags.LEAVES)
                .define('D', ATETags.Items.DIRT_LIKE_BLOCK_ITEMS)
                .unlockedBy("has_leaves", has(ItemTags.LEAVES))
                .save(recipeOutput);

    }

    public void buildDurabilityRecipes(RecipeOutput recipeOutput) {
        DurabilityShapelessRecipeBuilder.shapeless(Items.FARMLAND)
                .requires(ATETags.Items.DIRT_LIKE_BLOCK_ITEMS)
                .tool(Ingredient.of(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE))
                .durabilityPerItem(1)
                .group("farmland_from_dirt_like_blocks")
                .unlockedBy("has_dirt", has(Items.DIRT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "farmland_from_dirt_like"));

        DurabilityShapelessRecipeBuilder.shapeless(ATEBlocks.INFUSED_FARMLAND)
                .requires(ATEBlocks.MULCH)
                .tool(Ingredient.of(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE))
                .durabilityPerItem(1)
                .group("infused_farmlan")
                .unlockedBy("has_mulch", has(ATEBlocks.MULCH))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "infused_farmland_from_mulch"));
    }
}
