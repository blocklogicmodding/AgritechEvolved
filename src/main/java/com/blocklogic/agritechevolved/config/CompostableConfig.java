package com.blocklogic.agritechevolved.config;

import com.blocklogic.agritechevolved.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CompostableConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Set<String> compostableItems = new HashSet<>();

    public static void loadConfig() {
        LOGGER.info("CompostableConfig.loadConfig() invoked.");
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("agritechevolved/compostable.json");
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        try {
            String jsonString = Files.readString(configPath);
            CompostableConfigData configData = GSON.fromJson(jsonString, CompostableConfigData.class);
            processConfig(configData);
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to load compostable config file: {}", e.getMessage());
            LOGGER.info("Loading default compostable configuration instead");
            processConfig(getDefaultConfig());
        }

        CompostableOverrideConfig.loadOverrides(compostableItems);
    }

    private static void createDefaultConfig(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            CompostableConfigData defaultConfig = getDefaultConfig();
            String json = GSON.toJson(defaultConfig);
            Files.writeString(configPath, json);
            LOGGER.info("Created default compostable config at {}", configPath);
        } catch (IOException e) {
            LOGGER.error("Failed to create default compostable config file: {}", e.getMessage());
        }
    }

    private static CompostableConfigData getDefaultConfig() {
        LOGGER.info("Generating default compostable config.");
        CompostableConfigData config = new CompostableConfigData();
        config.compostableItems = new ArrayList<>();

        addVanillaCompostables(config.compostableItems);

        if (Config.enableMysticalAgriculture) addMysticalAgricultureCompostables(config.compostableItems);
        if (Config.enableFarmersDelight) addFarmersDelightCompostables(config.compostableItems);
        if (Config.enableArsNouveau) addArsNouveauCompostables(config.compostableItems);
        if (Config.enableArsElemental) addArsElementalCompostables(config.compostableItems);
        if (Config.enableSilentGear) addSilentGearCompostables(config.compostableItems);
        if (Config.enableImmersiveEngineering) addImmersiveEngineeringCompostables(config.compostableItems);
        if (Config.enableForbiddenArcanus) addforbiddenArcanusCompostables(config.compostableItems);
        if (Config.enableEvilCraft) addEvilCraftCompostables(config.compostableItems);
        if (Config.enableIntegratedDynamics) addIntegratedDynamicsCompostables(config.compostableItems);
        if (Config.enableOccultism) addOccultismCompostables(config.compostableItems);

        return config;
    }

    private static void addVanillaCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "minecraft:wheat_seeds", "minecraft:beetroot_seeds", "minecraft:melon_seeds", "minecraft:pumpkin_seeds",
                "minecraft:carrot", "minecraft:potato", "minecraft:oak_sapling", "minecraft:birch_sapling",
                "minecraft:spruce_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
                "minecraft:cherry_sapling", "minecraft:mangrove_propagule", "minecraft:azalea", "minecraft:flowering_azalea", "minecraft:nether_wart",
                "minecraft:sculk_vein", "minecraft:twisting_vines", "minecraft:weeping_vines", "minecraft:crimson_fungus",
                "minecraft:warped_fungus", "minecraft:red_mushroom", "minecraft:brown_mushroom", "minecraft:glow_lichen",
                "minecraft:sea_pickle", "minecraft:torchflower_seeds", "minecraft:chorus_fruit", "minecraft:chorus_flower",
                "minecraft:poisonous_potato", "minecraft:pitcher_pod", "minecraft:pitcher_crop", "minecraft:cocoa_beans",
                "minecraft:vine", "minecraft:spore_blossom", "minecraft:lily_pad", "minecraft:pink_petals", "minecraft:grass",
                "minecraft:fern", "minecraft:tall_grass", "minecraft:large_fern", "minecraft:cave_vines", "minecraft:moss_block",
                "minecraft:melon"
        ));

        items.addAll(Arrays.asList(
                "minecraft:wheat", "minecraft:beetroot", "minecraft:melon_slice", "minecraft:pumpkin",
                "minecraft:sugar_cane", "minecraft:bamboo", "minecraft:kelp", "minecraft:cactus",
                "minecraft:apple", "minecraft:sweet_berries", "minecraft:glow_berries"
        ));

        items.addAll(Arrays.asList(
                "minecraft:dirt", "minecraft:grass_block", "minecraft:coarse_dirt", "minecraft:podzol",
                "minecraft:mycelium", "minecraft:grass", "minecraft:fern", "minecraft:dead_bush",
                "minecraft:stick", "minecraft:leaves", "minecraft:oak_leaves", "minecraft:birch_leaves",
                "minecraft:spruce_leaves", "minecraft:jungle_leaves", "minecraft:acacia_leaves", "minecraft:dark_oak_leaves",
                "minecraft:cherry_leaves", "minecraft:azalea_leaves", "minecraft:flowering_azalea_leaves", "minecraft:mangrove_leaves",
                "minecraft:muddy_mangrove_roots", "minecraft:mangrove_roots"
        ));

        items.addAll(Arrays.asList(
                "minecraft:dandelion", "minecraft:poppy", "minecraft:blue_orchid", "minecraft:allium",
                "minecraft:azure_bluet", "minecraft:red_tulip", "minecraft:orange_tulip", "minecraft:white_tulip",
                "minecraft:pink_tulip", "minecraft:oxeye_daisy", "minecraft:cornflower", "minecraft:lily_of_the_valley",
                "minecraft:sunflower", "minecraft:lilac", "minecraft:rose_bush", "minecraft:peony",
                "minecraft:wither_rose", "minecraft:sunflower"
        ));

        items.addAll(Arrays.asList(
                "minecraft:rotten_flesh", "minecraft:bone", "minecraft:spider_eye",
                "minecraft:leather", "minecraft:feather", "minecraft:string"
        ));
    }

    private static void addMysticalAgricultureCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "mysticalagriculture:air_seeds",
                "mysticalagriculture:earth_seeds",
                "mysticalagriculture:water_seeds",
                "mysticalagriculture:fire_seeds",
                "mysticalagriculture:inferium_seeds",
                "mysticalagriculture:stone_seeds",
                "mysticalagriculture:dirt_seeds",
                "mysticalagriculture:wood_seeds",
                "mysticalagriculture:ice_seeds",
                "mysticalagriculture:deepslate_seeds"
        ));

        items.addAll(Arrays.asList(
                "mysticalagriculture:nature_seeds",
                "mysticalagriculture:dye_seeds",
                "mysticalagriculture:nether_seeds",
                "mysticalagriculture:coal_seeds",
                "mysticalagriculture:coral_seeds",
                "mysticalagriculture:honey_seeds",
                "mysticalagriculture:amethyst_seeds",
                "mysticalagriculture:pig_seeds",
                "mysticalagriculture:chicken_seeds",
                "mysticalagriculture:cow_seeds",
                "mysticalagriculture:sheep_seeds",
                "mysticalagriculture:squid_seeds",
                "mysticalagriculture:fish_seeds",
                "mysticalagriculture:slime_seeds",
                "mysticalagriculture:turtle_seeds",
                "mysticalagriculture:armadillo_seeds",
                "mysticalagriculture:rubber_seeds",
                "mysticalagriculture:silicon_seeds",
                "mysticalagriculture:sulfur_seeds",
                "mysticalagriculture:aluminum_seeds",
                "mysticalagriculture:saltpeter_seeds",
                "mysticalagriculture:apatite_seeds",
                "mysticalagriculture:grains_of_infinity_seeds",
                "mysticalagriculture:mystical_flower_seeds",
                "mysticalagriculture:marble_seeds",
                "mysticalagriculture:limestone_seeds",
                "mysticalagriculture:basalt_seeds",
                "mysticalagriculture:menril_seeds"
        ));

        items.addAll(Arrays.asList(
                "mysticalagriculture:iron_seeds",
                "mysticalagriculture:copper_seeds",
                "mysticalagriculture:nether_quartz_seeds",
                "mysticalagriculture:glowstone_seeds",
                "mysticalagriculture:redstone_seeds",
                "mysticalagriculture:obsidian_seeds",
                "mysticalagriculture:prismarine_seeds",
                "mysticalagriculture:zombie_seeds",
                "mysticalagriculture:skeleton_seeds",
                "mysticalagriculture:creeper_seeds",
                "mysticalagriculture:spider_seeds",
                "mysticalagriculture:rabbit_seeds",
                "mysticalagriculture:tin_seeds",
                "mysticalagriculture:bronze_seeds",
                "mysticalagriculture:zinc_seeds",
                "mysticalagriculture:brass_seeds",
                "mysticalagriculture:silver_seeds",
                "mysticalagriculture:lead_seeds",
                "mysticalagriculture:graphite_seeds",
                "mysticalagriculture:blizz_seeds",
                "mysticalagriculture:blitz_seeds",
                "mysticalagriculture:basalz_seeds",
                "mysticalagriculture:amethyst_bronze_seeds",
                "mysticalagriculture:slimesteel_seeds",
                "mysticalagriculture:pig_iron_seeds",
                "mysticalagriculture:copper_alloy_seeds",
                "mysticalagriculture:redstone_alloy_seeds",
                "mysticalagriculture:conductive_alloy_seeds",
                "mysticalagriculture:steeleaf_seeds",
                "mysticalagriculture:ironwood_seeds",
                "mysticalagriculture:sky_stone_seeds",
                "mysticalagriculture:certus_quartz_seeds",
                "mysticalagriculture:quartz_enriched_iron_seeds",
                "mysticalagriculture:manasteel_seeds",
                "mysticalagriculture:aquamarine_seeds"
        ));

        items.addAll(Arrays.asList(
                "mysticalagriculture:gold_seeds",
                "mysticalagriculture:lapis_lazuli_seeds",
                "mysticalagriculture:end_seeds",
                "mysticalagriculture:experience_seeds",
                "mysticalagriculture:breeze_seeds",
                "mysticalagriculture:blaze_seeds",
                "mysticalagriculture:ghast_seeds",
                "mysticalagriculture:enderman_seeds",
                "mysticalagriculture:steel_seeds",
                "mysticalagriculture:nickel_seeds",
                "mysticalagriculture:constantan_seeds",
                "mysticalagriculture:electrum_seeds",
                "mysticalagriculture:invar_seeds",
                "mysticalagriculture:uranium_seeds",
                "mysticalagriculture:ruby_seeds",
                "mysticalagriculture:sapphire_seeds",
                "mysticalagriculture:peridot_seeds",
                "mysticalagriculture:soulium_seeds",
                "mysticalagriculture:signalum_seeds",
                "mysticalagriculture:lumium_seeds",
                "mysticalagriculture:flux_infused_ingot_seeds",
                "mysticalagriculture:hop_graphite_seeds",
                "mysticalagriculture:cobalt_seeds",
                "mysticalagriculture:rose_gold_seeds",
                "mysticalagriculture:soularium_seeds",
                "mysticalagriculture:dark_steel_seeds",
                "mysticalagriculture:pulsating_alloy_seeds",
                "mysticalagriculture:energetic_alloy_seeds",
                "mysticalagriculture:elementium_seeds",
                "mysticalagriculture:osmium_seeds",
                "mysticalagriculture:fluorite_seeds",
                "mysticalagriculture:refined_glowstone_seeds",
                "mysticalagriculture:refined_obsidian_seeds",
                "mysticalagriculture:knightmetal_seeds",
                "mysticalagriculture:fiery_ingot_seeds",
                "mysticalagriculture:compressed_iron_seeds",
                "mysticalagriculture:starmetal_seeds",
                "mysticalagriculture:fluix_seeds",
                "mysticalagriculture:energized_steel_seeds",
                "mysticalagriculture:blazing_crystal_seeds"
        ));

        items.addAll(Arrays.asList(
                "mysticalagriculture:diamond_seeds",
                "mysticalagriculture:emerald_seeds",
                "mysticalagriculture:netherite_seeds",
                "mysticalagriculture:wither_skeleton_seeds",
                "mysticalagriculture:platinum_seeds",
                "mysticalagriculture:iridium_seeds",
                "mysticalagriculture:enderium_seeds",
                "mysticalagriculture:flux_infused_gem_seeds",
                "mysticalagriculture:manyullyn_seeds",
                "mysticalagriculture:queens_slime_seeds",
                "mysticalagriculture:hepatizon_seeds",
                "mysticalagriculture:vibrant_alloy_seeds",
                "mysticalagriculture:end_steel_seeds",
                "mysticalagriculture:terrasteel_seeds",
                "mysticalagriculture:rock_crystal_seeds",
                "mysticalagriculture:draconium_seeds",
                "mysticalagriculture:yellorium_seeds",
                "mysticalagriculture:cyanite_seeds",
                "mysticalagriculture:niotic_crystal_seeds",
                "mysticalagriculture:spirited_crystal_seeds",
                "mysticalagriculture:uraninite_seeds"
        ));
    }

    private static void addforbiddenArcanusCompostables(List<String> items) {
        items.addAll(Arrays.asList("forbidden_arcanus:aurum_sapling","forbidden_arcanus:growing_edelwood"));
    }

    private static void addFarmersDelightCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "farmersdelight:cabbage_seeds", "farmersdelight:tomato_seeds", "farmersdelight:onion",
                "farmersdelight:cabbage", "farmersdelight:tomato", "farmersdelight:rice"
        ));
    }

    private static void addArsNouveauCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "ars_nouveau:magebloom","ars_nouveau:magebloom_crop", "ars_nouveau:sourceberry",
                "ars_nouveau:blue_archwood_sapling","ars_nouveau:red_archwood_sapling", "ars_nouveau:purple_archwood_sapling","ars_nouveau:green_archwood_sapling"
        ));
    }

    private static void addArsElementalCompostables(List<String> items) {
        items.add("ars_elemental:yellow_archwood_sapling");
    }

    private static void addSilentGearCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "silentgear:flax_seeds", "silentgear:flax_flowers", "silentgear:fluffy_seeds",
                "silentgear:fluffy_puff", "silentgear:flax_fiber"
        ));
    }

    private static void addImmersiveEngineeringCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "immersiveengineering:seed", "immersiveengineering:hemp_fiber"
        ));
    }

    private static void addEvilCraftCompostables(List<String> items) {
        items.add("evilcraft:undead_sapling");
    }

    private static void addIntegratedDynamicsCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "integrateddynamics:menril_sapling", "integrateddynamics:menril_berries"
        ));
    }

    private static void addOccultismCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "occultism:otherworld_sapling", "occultism:datura_seeds", "occultism:datura"
        ));
    }

    private static void processConfig(CompostableConfigData configData) {
        compostableItems.clear();

        if (configData.compostableItems != null) {
            compostableItems.addAll(configData.compostableItems);
        }

        LOGGER.info("Loaded {} compostable items from config",
                compostableItems.size());
    }

    public static boolean isCompostable(String itemId) {
        return compostableItems.contains(itemId);
    }

    public static Set<String> getCompostableItems() {
        return new HashSet<>(compostableItems);
    }

    public static class CompostableConfigData {
        public List<String> compostableItems;
    }
}