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
    private static Set<String> infusableItems = new HashSet<>();

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

        CompostableOverrideConfig.loadOverrides(compostableItems, infusableItems);
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
        config.infusableItems = new ArrayList<>();

        // Add vanilla compostable items (seeds, crops, organic materials)
        addVanillaCompostables(config.compostableItems);

        // Add mod compostables based on enabled mods
        if (Config.enableMysticalAgriculture) addMysticalAgricultureCompostables(config.compostableItems);
        if (Config.enableMysticalAgradditions) addMysticalAgradditionsCompostables(config.compostableItems);
        if (Config.enableFarmersDelight) addFarmersDelightCompostables(config.compostableItems);
        if (Config.enableArsNouveau) addArsNouveauCompostables(config.compostableItems);
        if (Config.enableSilentGear) addSilentGearCompostables(config.compostableItems);
        if (Config.enableImmersiveEngineering) addImmersiveEngineeringCompostables(config.compostableItems);

        // Add vanilla infusable items (precious materials)
        addVanillaInfusables(config.infusableItems);

        // Add mod infusables based on enabled mods
        if (Config.enableMysticalAgriculture) addMysticalAgricultureInfusables(config.infusableItems);
        if (Config.enableFarmersDelight) addFarmersDelightInfusables(config.infusableItems);

        return config;
    }

    private static void addVanillaCompostables(List<String> items) {
        // Seeds and saplings
        items.addAll(Arrays.asList(
                "minecraft:wheat_seeds", "minecraft:beetroot_seeds", "minecraft:melon_seeds", "minecraft:pumpkin_seeds",
                "minecraft:carrot", "minecraft:potato", "minecraft:oak_sapling", "minecraft:birch_sapling",
                "minecraft:spruce_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
                "minecraft:cherry_sapling", "minecraft:mangrove_propagule"
        ));

        // Crops and foods
        items.addAll(Arrays.asList(
                "minecraft:wheat", "minecraft:beetroot", "minecraft:melon_slice", "minecraft:pumpkin",
                "minecraft:sugar_cane", "minecraft:bamboo", "minecraft:kelp", "minecraft:cactus",
                "minecraft:apple", "minecraft:sweet_berries", "minecraft:glow_berries"
        ));

        // Organic blocks and materials
        items.addAll(Arrays.asList(
                "minecraft:dirt", "minecraft:grass_block", "minecraft:coarse_dirt", "minecraft:podzol",
                "minecraft:mycelium", "minecraft:grass", "minecraft:fern", "minecraft:dead_bush",
                "minecraft:stick", "minecraft:leaves", "minecraft:oak_leaves", "minecraft:birch_leaves",
                "minecraft:spruce_leaves", "minecraft:jungle_leaves", "minecraft:acacia_leaves", "minecraft:dark_oak_leaves"
        ));

        // Flowers
        items.addAll(Arrays.asList(
                "minecraft:dandelion", "minecraft:poppy", "minecraft:blue_orchid", "minecraft:allium",
                "minecraft:azure_bluet", "minecraft:red_tulip", "minecraft:orange_tulip", "minecraft:white_tulip",
                "minecraft:pink_tulip", "minecraft:oxeye_daisy", "minecraft:cornflower", "minecraft:lily_of_the_valley",
                "minecraft:sunflower", "minecraft:lilac", "minecraft:rose_bush", "minecraft:peony"
        ));

        // Mob drops (organic)
        items.addAll(Arrays.asList(
                "minecraft:rotten_flesh", "minecraft:bone", "minecraft:spider_eye",
                "minecraft:leather", "minecraft:feather", "minecraft:string"
        ));
    }

    private static void addVanillaInfusables(List<String> items) {
        // Tier 1: Common materials
        items.addAll(Arrays.asList(
                "minecraft:raw_copper", "minecraft:raw_iron", "minecraft:coal", "minecraft:lapis_lazuli"
        ));

        // Tier 2: Uncommon materials
        items.addAll(Arrays.asList(
                "minecraft:raw_gold", "minecraft:amethyst_shard", "minecraft:redstone"
        ));

        // Tier 3: Rare materials
        items.addAll(Arrays.asList(
                "minecraft:diamond", "minecraft:emerald"
        ));
    }

    private static void addMysticalAgricultureCompostables(List<String> items) {
        // All Mystical Agriculture seeds are compostable
        String[] essenceTypes = {"inferium", "prudentium", "tertium", "imperium", "supremium"};
        for (String type : essenceTypes) {
            items.add("mysticalagriculture:" + type + "_seeds");
            items.add("mysticalagriculture:" + type + "_essence");
        }
    }

    private static void addMysticalAgradditionsCompostables(List<String> items) {
        items.add("mysticalagradditions:insanium_essence");
    }

    private static void addFarmersDelightCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "farmersdelight:cabbage_seeds", "farmersdelight:tomato_seeds", "farmersdelight:onion",
                "farmersdelight:cabbage", "farmersdelight:tomato", "farmersdelight:rice"
        ));
    }

    private static void addFarmersDelightInfusables(List<String> items) {
        items.addAll(Arrays.asList(
                "farmersdelight:organic_compost"
        ));
    }

    private static void addArsNouveauCompostables(List<String> items) {
        items.addAll(Arrays.asList(
                "ars_nouveau:magebloom","ars_nouveau:magebloom_crop", "ars_nouveau:sourceberry"
        ));
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

    private static void addMysticalAgricultureInfusables(List<String> items) {
        String[] essenceTypes = {"inferium", "prudentium", "tertium", "imperium", "supremium"};
        for (String type : essenceTypes) {
            items.add("mysticalagriculture:" + type + "_essence");
        }
    }

    private static void processConfig(CompostableConfigData configData) {
        compostableItems.clear();
        infusableItems.clear();

        if (configData.compostableItems != null) {
            compostableItems.addAll(configData.compostableItems);
        }

        if (configData.infusableItems != null) {
            infusableItems.addAll(configData.infusableItems);
        }

        LOGGER.info("Loaded {} compostable items and {} infusable items from config",
                compostableItems.size(), infusableItems.size());
    }

    // Public getter methods
    public static boolean isCompostable(String itemId) {
        return compostableItems.contains(itemId);
    }

    public static boolean isInfusable(String itemId) {
        return infusableItems.contains(itemId);
    }

    public static Set<String> getCompostableItems() {
        return new HashSet<>(compostableItems);
    }

    public static Set<String> getInfusableItems() {
        return new HashSet<>(infusableItems);
    }

    // Configuration data classes
    public static class CompostableConfigData {
        public List<String> compostableItems;
        public List<String> infusableItems;
    }
}