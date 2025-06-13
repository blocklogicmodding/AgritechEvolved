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

public class PlantablesConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<String, CropInfo> crops = new HashMap<>();
    private static Map<String, TreeInfo> trees = new HashMap<>();
    private static Map<String, SoilInfo> soils = new HashMap<>();
    private static Map<String, FertilizerInfo> fertilizers = new HashMap<>();

    public static void loadConfig() {
        LOGGER.info("PlantablesConfig.loadConfig() invoked.");
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("agritechevolved/plantables.json");
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath);
        }

        try {
            String jsonString = Files.readString(configPath);
            PlantablesConfigData configData = GSON.fromJson(jsonString, PlantablesConfigData.class);
            processConfig(configData);
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("Failed to load plantables config file: {}", e.getMessage());
            LOGGER.info("Loading default plantables configuration instead");
            processConfig(getDefaultConfig());
        }

        PlantablesOverrideConfig.loadOverrides(crops, trees, soils, soils, fertilizers);
    }

    private static void createDefaultConfig(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            PlantablesConfigData defaultConfig = getDefaultConfig();
            String json = GSON.toJson(defaultConfig);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            LOGGER.error("Failed to create default config file: {}", e.getMessage());
        }
    }

    private static PlantablesConfigData getDefaultConfig() {
        LOGGER.info("Generating default plantables config.");

        PlantablesConfigData config = new PlantablesConfigData();

        List<CropEntry> defaultCrops = new ArrayList<>();
        addVanillaCrops(defaultCrops);

        if (Config.enableMysticalAgriculture) {
            LOGGER.info("Adding Mystical Agriculture crops to AgriTech:Evolved config");
            addMysticalAgricultureCrops(defaultCrops);
        }

        if (Config.enableFarmersDelight) {
            LOGGER.info("Adding Farmer's Delight crops to AgriTech:Evolved config");
            addFarmersDelightCrops(defaultCrops);
        }

        if (Config.enableArsNouveau) {
            LOGGER.info("Adding Ars Nouveau crops to AgriTech:Evolved config");
            addArsNouveauCrops(defaultCrops);
        }

        if (Config.enableSilentGear) {
            LOGGER.info("Adding Silent Gear crops to AgriTech:Evolved config");
            addSilentGearCrops(defaultCrops);
        }

        if (Config.enableHexerei) {
            LOGGER.info("Adding Hexerei crops to AgriTech:Evolved config");
            addHexereiCrops(defaultCrops);
        }

        config.allowedCrops = defaultCrops;

        List<TreeEntry> defaultTrees = new ArrayList<>();
        addVanillaTrees(defaultTrees);

        if (Config.enableArsElemental) {
            LOGGER.info("Adding Ars Nouveau Archwood trees to AgriTech:Evolved config");
            addArsElementalTrees(defaultTrees);
        }

        if (Config.enableArsNouveau) {
            LOGGER.info("Adding Ars Nouveau Archwood trees to AgriTech:Evolved config");
            addArsNouveauTrees(defaultTrees);
        }

        if (Config.enableEvilCraft) {
            LOGGER.info("Adding Evilcraft trees to AgriTech:Evolved config");
            addEvilCraftTrees(defaultTrees);
        }

        if (Config.enableForbiddenArcanus) {
            LOGGER.info("Adding Forbidden Arcanus trees to AgriTech:Evolved config");
            addForbiddenArcanusTrees(defaultTrees);
        }

        if (Config.enableIntegratedDynamics) {
            LOGGER.info("Adding Menril trees to AgriTech:Evolved config");
            addIntegratedDynamicsTrees(defaultTrees);
        }

        if (Config.enableOccultism) {
            LOGGER.info("Adding Occultism trees to AgriTech:Evolved config");
            addOccultismTrees(defaultTrees);
        }

        if (Config.enableHexerei) {
            LOGGER.info("Adding Hexerei trees to AgriTech:Evolved config");
            addHexereiTrees(defaultTrees);
        }

        config.allowedTrees = defaultTrees;

        List<SoilEntry> defaultSoils = new ArrayList<>();
        addVanillaSoils(defaultSoils);

        if (Config.enableMysticalAgriculture) {
            LOGGER.info("Adding Mystical Agriculture soils to AgriTech:Evolved config");
            addMysticalAgricultureSoils(defaultSoils);
        }

        if (Config.enableFarmersDelight) {
            LOGGER.info("Adding Farmer's Delight soils to AgriTech:Evolved config");
            addFarmersDelightSoils(defaultSoils);
        }

        if(Config.enableAgritechEvolved) {
            LOGGER.info("Adding Agritech: Evolved soils to AgriTech:Evolved config");
            addAgritechEvolvedSoils(defaultSoils);
        }

        if(Config.enableJustDireThing) {
            LOGGER.info("Adding Just Dire Things soils to AgriTech:Evolved config");
            addJustDireThingsSoils(defaultSoils);
        }

        if(Config.enableImmersiveEngineering) {
            LOGGER.info("Adding Immersive Engineering Hemp Fiber to AgriTech:Evolved config");
            addImmersiveEngineering(defaultCrops);
        }

        config.allowedSoils = defaultSoils;

        List<FertilizerEntry> defaultFertilizers = new ArrayList<>();
        addVanillaFertilizers(defaultFertilizers);

        if (Config.enableImmersiveEngineering) {
            LOGGER.info("Adding Immersive Engineering fertilizer to AgriTech:Evolved config");
            addImmersiveEngineeringFertilizers(defaultFertilizers);
        }

        if (Config.enableForbiddenArcanus) {
            LOGGER.info("Adding Forbidden Arcanus fertilizer to AgriTech:Evolved config");
            addForbiddenArcanusFertilizers(defaultFertilizers);
        }

        if (Config.enableMysticalAgriculture) {
            LOGGER.info("Adding Mystical Agriculture fertilizer to AgriTech:Evolved config");
            addMysticalAgricultureFertilizers(defaultFertilizers);
        }

        if(Config.enableAgritechEvolved) {
            LOGGER.info("Adding Agritech: Evolved biomass to AgriTech:Evolved config");
            addAgritechEvolvedFertilizer(defaultFertilizers);
        }

        config.allowedFertilizers = defaultFertilizers;

        return config;
    }

    private static void addVanillaCrops(List<CropEntry> crops) {
        // Wheat
        CropEntry wheat = new CropEntry();
        wheat.seed = "minecraft:wheat_seeds";
        wheat.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland");
        wheat.drops = new ArrayList<>();

        DropEntry wheatDrop = new DropEntry();
        wheatDrop.item = "minecraft:wheat";
        wheatDrop.count = new CountRange(1, 1);
        wheat.drops.add(wheatDrop);

        DropEntry wheatSeedsDrop = new DropEntry();
        wheatSeedsDrop.item = "minecraft:wheat_seeds";
        wheatSeedsDrop.count = new CountRange(1, 2);
        wheatSeedsDrop.chance = 0.5f;
        wheat.drops.add(wheatSeedsDrop);

        crops.add(wheat);

        // Beetroot
        CropEntry beetroot = new CropEntry();
        beetroot.seed = "minecraft:beetroot_seeds";
        beetroot.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland");
        beetroot.drops = new ArrayList<>();

        DropEntry beetrootDrop = new DropEntry();
        beetrootDrop.item = "minecraft:beetroot";
        beetrootDrop.count = new CountRange(1, 1);
        beetroot.drops.add(beetrootDrop);

        DropEntry beetrootSeedsDrop = new DropEntry();
        beetrootSeedsDrop.item = "minecraft:beetroot_seeds";
        beetrootSeedsDrop.count = new CountRange(1, 2);
        beetrootSeedsDrop.chance = 0.5f;
        beetroot.drops.add(beetrootSeedsDrop);

        crops.add(beetroot);

        // Carrot
        CropEntry carrot = new CropEntry();
        carrot.seed = "minecraft:carrot";
        carrot.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland");
        carrot.drops = new ArrayList<>();

        DropEntry carrotDrop = new DropEntry();
        carrotDrop.item = "minecraft:carrot";
        carrotDrop.count = new CountRange(2, 5);
        carrot.drops.add(carrotDrop);

        crops.add(carrot);

        // Potato
        CropEntry potato = new CropEntry();
        potato.seed = "minecraft:potato";
        potato.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland");
        potato.drops = new ArrayList<>();

        DropEntry potatoDrop = new DropEntry();
        potatoDrop.item = "minecraft:potato";
        potatoDrop.count = new CountRange(2, 5);
        potato.drops.add(potatoDrop);

        DropEntry poisonousPotatoDrop = new DropEntry();
        poisonousPotatoDrop.item = "minecraft:poisonous_potato";
        poisonousPotatoDrop.count = new CountRange(1, 1);
        poisonousPotatoDrop.chance = 0.02f;
        potato.drops.add(poisonousPotatoDrop);

        crops.add(potato);

        // Melon
        CropEntry melon = new CropEntry();
        melon.seed = "minecraft:melon_seeds";
        melon.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        melon.drops = new ArrayList<>();

        DropEntry melonSliceDrop = new DropEntry();
        melonSliceDrop.item = "minecraft:melon_slice";
        melonSliceDrop.count = new CountRange(3, 7);
        melon.drops.add(melonSliceDrop);

        crops.add(melon);

        // Pumpkin
        CropEntry pumpkin = new CropEntry();
        pumpkin.seed = "minecraft:pumpkin_seeds";
        pumpkin.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        pumpkin.drops = new ArrayList<>();

        DropEntry pumpkinDrop = new DropEntry();
        pumpkinDrop.item = "minecraft:pumpkin";
        pumpkinDrop.count = new CountRange(1, 1);
        pumpkin.drops.add(pumpkinDrop);

        crops.add(pumpkin);

        // Sugar Cane
        CropEntry sugarCane = new CropEntry();
        sugarCane.seed = "minecraft:sugar_cane";
        sugarCane.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:sand",
                "minecraft:red_sand",
                "agritechevolved:mulch"
        );
        sugarCane.drops = new ArrayList<>();

        DropEntry sugarCaneDrop = new DropEntry();
        sugarCaneDrop.item = "minecraft:sugar_cane";
        sugarCaneDrop.count = new CountRange(1, 3);
        sugarCane.drops.add(sugarCaneDrop);

        crops.add(sugarCane);

        // Cactus
        CropEntry cactus = new CropEntry();
        cactus.seed = "minecraft:cactus";
        cactus.validSoils = List.of(
                "minecraft:sand",
                "minecraft:red_sand",
                "agritechevolved:mulch"
        );
        cactus.drops = new ArrayList<>();

        DropEntry cactusDrop = new DropEntry();
        cactusDrop.item = "minecraft:cactus";
        cactusDrop.count = new CountRange(1, 3);
        cactus.drops.add(cactusDrop);

        crops.add(cactus);

        // Bamboo
        CropEntry bamboo = new CropEntry();
        bamboo.seed = "minecraft:bamboo";
        bamboo.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",
                "agritechevolved:mulch",
                "farmersdelight:rich_soil",
                "farmersdelight:organic_compost"
        );
        bamboo.drops = new ArrayList<>();

        DropEntry bambooDrop = new DropEntry();
        bambooDrop.item = "minecraft:bamboo";
        bambooDrop.count = new CountRange(2, 4);
        bamboo.drops.add(bambooDrop);

        crops.add(bamboo);

        // Sweet Berries
        CropEntry sweetBerries = new CropEntry();
        sweetBerries.seed = "minecraft:sweet_berries";
        sweetBerries.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland",
                "farmersdelight:rich_soil",
                "farmersdelight:organic_compost"
        );
        sweetBerries.drops = new ArrayList<>();

        DropEntry sweetBerriesDrop = new DropEntry();
        sweetBerriesDrop.item = "minecraft:sweet_berries";
        sweetBerriesDrop.count = new CountRange(2, 4);
        sweetBerries.drops.add(sweetBerriesDrop);

        crops.add(sweetBerries);

        // Glow Berries
        CropEntry glowBerries = new CropEntry();
        glowBerries.seed = "minecraft:glow_berries";
        glowBerries.validSoils = List.of(
                "minecraft:moss_block"
        );
        glowBerries.drops = new ArrayList<>();

        DropEntry glowBerriesDrop = new DropEntry();
        glowBerriesDrop.item = "minecraft:glow_berries";
        glowBerriesDrop.count = new CountRange(2, 4);
        glowBerries.drops.add(glowBerriesDrop);

        crops.add(glowBerries);

        // Nether Wart
        CropEntry netherWart = new CropEntry();
        netherWart.seed = "minecraft:nether_wart";
        netherWart.validSoils = List.of("minecraft:soul_sand");
        netherWart.drops = new ArrayList<>();

        DropEntry netherWartDrop = new DropEntry();
        netherWartDrop.item = "minecraft:nether_wart";
        netherWartDrop.count = new CountRange(1, 3);
        netherWart.drops.add(netherWartDrop);

        crops.add(netherWart);

        // Chorus Fruit
        CropEntry chorusFlower = new CropEntry();
        chorusFlower.seed = "minecraft:chorus_flower";
        chorusFlower.validSoils = List.of("minecraft:end_stone");
        chorusFlower.drops = new ArrayList<>();

        DropEntry chorusFruitDrop = new DropEntry();
        chorusFruitDrop.item = "minecraft:chorus_fruit";
        chorusFruitDrop.count = new CountRange(1, 3);
        chorusFlower.drops.add(chorusFruitDrop);

        DropEntry chorusFlowerDrop = new DropEntry();
        chorusFlowerDrop.item = "minecraft:chorus_flower";
        chorusFlowerDrop.count = new CountRange(1, 1);
        chorusFlowerDrop.chance = 0.02f;
        chorusFlower.drops.add(chorusFlowerDrop);

        crops.add(chorusFlower);

        // Kelp
        CropEntry kelp = new CropEntry();
        kelp.seed = "minecraft:kelp";
        kelp.validSoils = List.of("minecraft:mud");
        kelp.drops = new ArrayList<>();

        DropEntry kelpDrop = new DropEntry();
        kelpDrop.item = "minecraft:kelp";
        kelpDrop.count = new CountRange(1, 2);
        kelp.drops.add(kelpDrop);

        crops.add(kelp);

        // Brown Mushroom
        CropEntry brownMushroom = new CropEntry();
        brownMushroom.seed = "minecraft:brown_mushroom";
        brownMushroom.validSoils = List.of(
                "minecraft:mycelium",
                "minecraft:podzol",
                "agritechevolved:mulch",
                "farmersdelight:rich_soil",
                "farmersdelight:organic_compost"
        );
        brownMushroom.drops = new ArrayList<>();

        DropEntry brownMushroomDrop = new DropEntry();
        brownMushroomDrop.item = "minecraft:brown_mushroom";
        brownMushroomDrop.count = new CountRange(1, 1);
        brownMushroom.drops.add(brownMushroomDrop);

        crops.add(brownMushroom);

        // Red Mushroom
        CropEntry redMushroom = new CropEntry();
        redMushroom.seed = "minecraft:red_mushroom";
        redMushroom.validSoils = List.of(
                "minecraft:mycelium",
                "minecraft:podzol",
                "agritechevolved:mulch",
                "farmersdelight:rich_soil",
                "farmersdelight:organic_compost"
        );
        redMushroom.drops = new ArrayList<>();

        DropEntry redMushroomDrop = new DropEntry();
        redMushroomDrop.item = "minecraft:red_mushroom";
        redMushroomDrop.count = new CountRange(1, 1);
        redMushroom.drops.add(redMushroomDrop);

        crops.add(redMushroom);

        // Cocoa
        CropEntry cocoa = new CropEntry();
        cocoa.seed = "minecraft:cocoa_beans";
        cocoa.validSoils = List.of(
                "minecraft:jungle_log",
                "minecraft:jungle_wood",
                "minecraft:stripped_jungle_log",
                "minecraft:stripped_jungle_wood"
        );
        cocoa.drops = new ArrayList<>();

        DropEntry cocoaDrop = new DropEntry();
        cocoaDrop.item = "minecraft:cocoa_beans";
        cocoaDrop.count = new CountRange(1, 3);
        cocoa.drops.add(cocoaDrop);

        crops.add(cocoa);

        //pitcher_plant
        CropEntry pitcherCrop = new CropEntry();
        pitcherCrop.seed = "minecraft:pitcher_pod";
        pitcherCrop.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        pitcherCrop.drops = new ArrayList<>();

        DropEntry pitcherCropDrop = new DropEntry();
        pitcherCropDrop.item = "minecraft:pitcher_plant";
        pitcherCropDrop.count = new CountRange(1, 1);
        pitcherCrop.drops.add(pitcherCropDrop);

        crops.add(pitcherCrop);

        //torchflower
        CropEntry torchFlower = new CropEntry();
        torchFlower.seed = "minecraft:torchflower_seeds";
        torchFlower.validSoils = List.of(
                "minecraft:farmland",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        torchFlower.drops = new ArrayList<>();

        DropEntry torchFlowerDrop = new DropEntry();
        torchFlowerDrop.item = "minecraft:torchflower";
        torchFlowerDrop.count = new CountRange(1, 1);
        torchFlower.drops.add(torchFlowerDrop);

        crops.add(torchFlower);

        // Flowers Start
        CropEntry allium = new CropEntry();
        allium.seed = "minecraft:allium";
        allium.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        allium.drops = new ArrayList<>();

        DropEntry alliumDrop = new DropEntry();
        alliumDrop.item = "minecraft:allium";
        alliumDrop.count = new CountRange(1, 1);
        allium.drops.add(alliumDrop);

        crops.add(allium);

        CropEntry azureBluet = new CropEntry();
        azureBluet.seed = "minecraft:azure_bluet";
        azureBluet.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        azureBluet.drops = new ArrayList<>();

        DropEntry azureBluetDrop = new DropEntry();
        azureBluetDrop.item = "minecraft:azure_bluet";
        azureBluetDrop.count = new CountRange(1, 1);
        azureBluet.drops.add(azureBluetDrop);

        crops.add(azureBluet);

        CropEntry blueOrchid = new CropEntry();
        blueOrchid.seed = "minecraft:blue_orchid";
        blueOrchid.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        blueOrchid.drops = new ArrayList<>();

        DropEntry blueOrchidDrop = new DropEntry();
        blueOrchidDrop.item = "minecraft:blue_orchid";
        blueOrchidDrop.count = new CountRange(1, 1);
        blueOrchid.drops.add(blueOrchidDrop);

        crops.add(blueOrchid);

        CropEntry cornflower = new CropEntry();
        cornflower.seed = "minecraft:cornflower";
        cornflower.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        cornflower.drops = new ArrayList<>();

        DropEntry cornflowerDrop = new DropEntry();
        cornflowerDrop.item = "minecraft:cornflower";
        cornflowerDrop.count = new CountRange(1, 1);
        cornflower.drops.add(cornflowerDrop);

        crops.add(cornflower);

        CropEntry dandelion = new CropEntry();
        dandelion.seed = "minecraft:dandelion";
        dandelion.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        dandelion.drops = new ArrayList<>();

        DropEntry dandelionDrop = new DropEntry();
        dandelionDrop.item = "minecraft:dandelion";
        dandelionDrop.count = new CountRange(1, 1);
        dandelion.drops.add(dandelionDrop);

        crops.add(dandelion);

        CropEntry lilyOfTheValley = new CropEntry();
        lilyOfTheValley.seed = "minecraft:lily_of_the_valley";
        lilyOfTheValley.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        lilyOfTheValley.drops = new ArrayList<>();

        DropEntry lilyOfTheValleyDrop = new DropEntry();
        lilyOfTheValleyDrop.item = "minecraft:lily_of_the_valley";
        lilyOfTheValleyDrop.count = new CountRange(1, 1);
        lilyOfTheValley.drops.add(lilyOfTheValleyDrop);

        crops.add(lilyOfTheValley);

        CropEntry oxeyeDaisy = new CropEntry();
        oxeyeDaisy.seed = "minecraft:oxeye_daisy";
        oxeyeDaisy.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        oxeyeDaisy.drops = new ArrayList<>();

        DropEntry oxeyeDaisyDrop = new DropEntry();
        oxeyeDaisyDrop.item = "minecraft:oxeye_daisy";
        oxeyeDaisyDrop.count = new CountRange(1, 1);
        oxeyeDaisy.drops.add(oxeyeDaisyDrop);

        crops.add(oxeyeDaisy);

        CropEntry poppy = new CropEntry();
        poppy.seed = "minecraft:poppy";
        poppy.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        poppy.drops = new ArrayList<>();

        DropEntry poppyDrop = new DropEntry();
        poppyDrop.item = "minecraft:poppy";
        poppyDrop.count = new CountRange(1, 1);
        poppy.drops.add(poppyDrop);

        crops.add(poppy);

        CropEntry redTulip = new CropEntry();
        redTulip.seed = "minecraft:red_tulip";
        redTulip.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        redTulip.drops = new ArrayList<>();

        DropEntry redTulipDrop = new DropEntry();
        redTulipDrop.item = "minecraft:red_tulip";
        redTulipDrop.count = new CountRange(1, 1);
        redTulip.drops.add(redTulipDrop);

        crops.add(redTulip);

        CropEntry orangeTulip = new CropEntry();
        orangeTulip.seed = "minecraft:orange_tulip";
        orangeTulip.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        orangeTulip.drops = new ArrayList<>();

        DropEntry orangeTulipDrop = new DropEntry();
        orangeTulipDrop.item = "minecraft:orange_tulip";
        orangeTulipDrop.count = new CountRange(1, 1);
        orangeTulip.drops.add(orangeTulipDrop);

        crops.add(orangeTulip);

        CropEntry whiteTulip = new CropEntry();
        whiteTulip.seed = "minecraft:white_tulip";
        whiteTulip.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        whiteTulip.drops = new ArrayList<>();

        DropEntry whiteTulipDrop = new DropEntry();
        whiteTulipDrop.item = "minecraft:white_tulip";
        whiteTulipDrop.count = new CountRange(1, 1);
        whiteTulip.drops.add(whiteTulipDrop);

        crops.add(whiteTulip);

        CropEntry pinkTulip = new CropEntry();
        pinkTulip.seed = "minecraft:pink_tulip";
        pinkTulip.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        pinkTulip.drops = new ArrayList<>();

        DropEntry pinkTulipDrop = new DropEntry();
        pinkTulipDrop.item = "minecraft:pink_tulip";
        pinkTulipDrop.count = new CountRange(1, 1);
        pinkTulip.drops.add(pinkTulipDrop);

        crops.add(pinkTulip);

        CropEntry witherRose = new CropEntry();
        witherRose.seed = "minecraft:wither_rose";
        witherRose.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        witherRose.drops = new ArrayList<>();

        DropEntry witherRoseDrop = new DropEntry();
        witherRoseDrop.item = "minecraft:wither_rose";
        witherRoseDrop.count = new CountRange(1, 1);
        witherRose.drops.add(witherRoseDrop);

        crops.add(witherRose);

        CropEntry lilac = new CropEntry();
        lilac.seed = "minecraft:lilac";
        lilac.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        lilac.drops = new ArrayList<>();

        DropEntry lilacDrop = new DropEntry();
        lilacDrop.item = "minecraft:lilac";
        lilacDrop.count = new CountRange(1, 1);
        lilac.drops.add(lilacDrop);

        crops.add(lilac);

        CropEntry peony = new CropEntry();
        peony.seed = "minecraft:peony";
        peony.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        peony.drops = new ArrayList<>();

        DropEntry peonyDrop = new DropEntry();
        peonyDrop.item = "minecraft:peony";
        peonyDrop.count = new CountRange(1, 1);
        peony.drops.add(peonyDrop);

        crops.add(peony);

        CropEntry roseBush = new CropEntry();
        roseBush.seed = "minecraft:rose_bush";
        roseBush.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        roseBush.drops = new ArrayList<>();

        DropEntry roseBushDrop = new DropEntry();
        roseBushDrop.item = "minecraft:rose_bush";
        roseBushDrop.count = new CountRange(1, 1);
        roseBush.drops.add(roseBushDrop);

        crops.add(roseBush);

        CropEntry sunflower = new CropEntry();
        sunflower.seed = "minecraft:sunflower";
        sunflower.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",

                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",

                "mysticalagradditions:insanium_farmland",

                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",

                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",

                "farmersdelight:rich_soil_farmland"
        );
        sunflower.drops = new ArrayList<>();

        DropEntry sunflowerDrop = new DropEntry();
        sunflowerDrop.item = "minecraft:sunflower";
        sunflowerDrop.count = new CountRange(1, 1);
        sunflower.drops.add(sunflowerDrop);

        crops.add(sunflower);

        // Flowers END

    }

    private static void addMysticalAgricultureCrops(List<CropEntry> crops) {
        // Tier 1 crops - can grow on any valid MA farmland
        String[] tier1Seeds = {
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
        };

        List<String> tier1Soils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );

        for (String seedId : tier1Seeds) {
            addMysticalAgricultureCrop(crops, seedId, tier1Soils);
        }

        // Tier 2 crops - require at least prudentium farmland
        String[] tier2Seeds = {
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
        };

        List<String> tier2Soils = List.of(
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4"
        );

        for (String seedId : tier2Seeds) {
            addMysticalAgricultureCrop(crops, seedId, tier2Soils);
        }

        // Tier 3 crops - require at least tertium farmland
        String[] tier3Seeds = {
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
        };

        List<String> tier3Soils = List.of(
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4"
        );

        for (String seedId : tier3Seeds) {
            addMysticalAgricultureCrop(crops, seedId, tier3Soils);
        }

        // Tier 4 crops - require at least imperium farmland
        String[] tier4Seeds = {
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
        };

        List<String> tier4Soils = List.of(
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4"
        );

        for (String seedId : tier4Seeds) {
            addMysticalAgricultureCrop(crops, seedId, tier4Soils);
        }

        // Tier 5 crops - require supremium farmland
        String[] tier5Seeds = {
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
        };

        List<String> tier5Soils = List.of(
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier4"
        );

        for (String seedId : tier5Seeds) {
            addMysticalAgricultureCrop(crops, seedId, tier5Soils);
        }

        // Special crops that require cruxes
        if (Config.enableMysticalAgradditions) {
            addSpecialCruxCrop(crops, "mysticalagriculture:nether_star_seeds", "mysticalagradditions:nether_star_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:dragon_egg_seeds", "mysticalagradditions:dragon_egg_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:gaia_spirit_seeds", "mysticalagradditions:gaia_spirit_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:awakened_draconium_seeds", "mysticalagradditions:awakened_draconium_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:neutronium_seeds", "mysticalagradditions:neutronium_crux");
            addSpecialCruxCrop(crops, "mysticalagriculture:nitro_crystal_seeds", "mysticalagradditions:nitro_crystal_crux");
        }
    }

    private static void addSpecialCruxCrop(List<CropEntry> crops, String seedId, String cruxId) {
        CropEntry crop = new CropEntry();
        crop.seed = seedId;
        crop.validSoils = List.of(cruxId);
        crop.drops = new ArrayList<>();

        String essence = seedId.replace("_seeds", "_essence");

        DropEntry essenceDrop = new DropEntry();
        essenceDrop.item = essence;
        essenceDrop.count = new CountRange(1, 1);
        essenceDrop.chance = 1.0f;
        crop.drops.add(essenceDrop);

        crops.add(crop);
    }

    private static void addMysticalAgricultureCrop(List<CropEntry> crops, String seedId, List<String> validSoils) {
        CropEntry crop = new CropEntry();
        crop.seed = seedId;
        crop.validSoils = new ArrayList<>(validSoils);
        crop.drops = new ArrayList<>();

        String essence = seedId.replace("_seeds", "_essence");

        DropEntry essenceDrop = new DropEntry();
        essenceDrop.item = essence;
        essenceDrop.count = new CountRange(1, 1);
        essenceDrop.chance = 1.0f;
        crop.drops.add(essenceDrop);

        DropEntry seedDrop = new DropEntry();
        seedDrop.item = seedId;
        seedDrop.count = new CountRange(1, 1);
        seedDrop.chance = 0.2f;
        crop.drops.add(seedDrop);

        crops.add(crop);
    }

    private static void addFarmersDelightCrops(List<CropEntry> crops) {
        // Cabbage
        CropEntry cabbage = new CropEntry();
        cabbage.seed = "farmersdelight:cabbage_seeds";
        cabbage.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        cabbage.drops = new ArrayList<>();

        DropEntry cabbageDrop = new DropEntry();
        cabbageDrop.item = "farmersdelight:cabbage";
        cabbageDrop.count = new CountRange(1, 1);
        cabbageDrop.chance = 1.0f;
        cabbage.drops.add(cabbageDrop);

        DropEntry cabbageSeedsDrop = new DropEntry();
        cabbageSeedsDrop.item = "farmersdelight:cabbage_seeds";
        cabbageSeedsDrop.count = new CountRange(1, 2);
        cabbageSeedsDrop.chance = 1.0f;
        cabbage.drops.add(cabbageSeedsDrop);

        crops.add(cabbage);

        // Tomato
        CropEntry tomato = new CropEntry();
        tomato.seed = "farmersdelight:tomato_seeds";
        tomato.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        tomato.drops = new ArrayList<>();

        DropEntry tomatoDrop = new DropEntry();
        tomatoDrop.item = "farmersdelight:tomato";
        tomatoDrop.count = new CountRange(1, 2);
        tomatoDrop.chance = 1.0f;
        tomato.drops.add(tomatoDrop);

        crops.add(tomato);

        // Onion
        CropEntry onion = new CropEntry();
        onion.seed = "farmersdelight:onion";
        onion.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        onion.drops = new ArrayList<>();

        DropEntry onionDrop = new DropEntry();
        onionDrop.item = "farmersdelight:onion";
        onionDrop.count = new CountRange(1, 3);
        onionDrop.chance = 1.0f;
        onion.drops.add(onionDrop);

        crops.add(onion);
    }

    private static void addImmersiveEngineering(List<CropEntry> crops) {
        // Immersive Engineering Hemp Fiber
        CropEntry immersiveHempFiber = new CropEntry();
        immersiveHempFiber.seed = "immersiveengineering:seed";
        immersiveHempFiber.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        immersiveHempFiber.drops = new ArrayList<>();

        DropEntry immersiveHempDrop = new DropEntry();
        immersiveHempDrop.item = "immersiveengineering:hemp_fiber";
        immersiveHempDrop.count = new CountRange(4, 8);
        immersiveHempDrop.chance = 1.0f;
        immersiveHempFiber.drops.add(immersiveHempDrop);

        DropEntry immersiveSeedDrop = new DropEntry();
        immersiveSeedDrop.item = "immersiveengineering:seed";
        immersiveSeedDrop.count = new CountRange(1, 1);
        immersiveSeedDrop.chance = 0.5f;
        immersiveHempFiber.drops.add(immersiveSeedDrop);

        crops.add(immersiveHempFiber);

    }

    private static void addArsNouveauCrops(List<CropEntry> crops) {
        // Magebloom
        CropEntry magebloom = new CropEntry();
        magebloom.seed = "ars_nouveau:magebloom_crop";
        magebloom.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        magebloom.drops = new ArrayList<>();

        DropEntry magebloomDrop = new DropEntry();
        magebloomDrop.item = "ars_nouveau:magebloom";
        magebloomDrop.count = new CountRange(1, 1);
        magebloomDrop.chance = 1.0f;
        magebloom.drops.add(magebloomDrop);

        crops.add(magebloom);

        // Sourceberry Bush
        CropEntry sourceberry = new CropEntry();
        sourceberry.seed = "ars_nouveau:sourceberry_bush";
        sourceberry.validSoils = List.of(
                "minecraft:farmland",
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:rooted_dirt",
                "minecraft:coarse_dirt",
                "minecraft:podzol",
                "minecraft:mycelium",
                "minecraft:mud",
                "minecraft:moss_block",
                "minecraft:muddy_mangrove_roots",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland",
                "farmersdelight:organic_compost",
                "farmersdelight:rich_soil"
        );
        sourceberry.drops = new ArrayList<>();

        DropEntry sourceberryDrop = new DropEntry();
        sourceberryDrop.item = "ars_nouveau:sourceberry_bush";
        sourceberryDrop.count = new CountRange(2, 4);
        sourceberryDrop.chance = 1.0f;
        sourceberry.drops.add(sourceberryDrop);

        crops.add(sourceberry);
    }

    private static void addSilentGearCrops(List<CropEntry> crops) {
        // Fluffy Puff
        CropEntry fluffyPuff = new CropEntry();
        fluffyPuff.seed = "silentgear:fluffy_seeds";
        fluffyPuff.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        fluffyPuff.drops = new ArrayList<>();

        DropEntry fluffyPuffDrop = new DropEntry();
        fluffyPuffDrop.item = "silentgear:fluffy_puff";
        fluffyPuffDrop.count = new CountRange(1, 4);
        fluffyPuffDrop.chance = 1.0f;
        fluffyPuff.drops.add(fluffyPuffDrop);

        DropEntry fluffySeedsDrop = new DropEntry();
        fluffySeedsDrop.item = "silentgear:fluffy_seeds";
        fluffySeedsDrop.count = new CountRange(1, 1);
        fluffySeedsDrop.chance = 1.0f;
        fluffyPuff.drops.add(fluffySeedsDrop);

        crops.add(fluffyPuff);

        // Flax
        CropEntry flax = new CropEntry();
        flax.seed = "silentgear:flax_seeds";
        flax.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        flax.drops = new ArrayList<>();

        DropEntry flaxFiberDrop = new DropEntry();
        flaxFiberDrop.item = "silentgear:flax_fiber";
        flaxFiberDrop.count = new CountRange(1, 4);
        flaxFiberDrop.chance = 1.0f;
        flax.drops.add(flaxFiberDrop);

        DropEntry flaxSeedsDrop = new DropEntry();
        flaxSeedsDrop.item = "silentgear:flax_seeds";
        flaxSeedsDrop.count = new CountRange(1, 1);
        flaxSeedsDrop.chance = 0.2f;
        flax.drops.add(flaxSeedsDrop);

        DropEntry flaxFlowersDrop = new DropEntry();
        flaxFlowersDrop.item = "silentgear:flax_flowers";
        flaxFlowersDrop.count = new CountRange(1, 1);
        flaxFlowersDrop.chance = 0.2f;
        flax.drops.add(flaxFlowersDrop);

        crops.add(flax);
    }

    private static void addHexereiCrops(List<CropEntry> crops) {
        // Hexerei Sage
        CropEntry hexSage = new CropEntry();
        hexSage.seed = "hexerei:sage_seed";
        hexSage.validSoils = List.of(
                "minecraft:farmland",
                "mysticalagriculture:inferium_farmland",
                "mysticalagriculture:prudentium_farmland",
                "mysticalagriculture:tertium_farmland",
                "mysticalagriculture:imperium_farmland",
                "mysticalagriculture:supremium_farmland",
                "mysticalagradditions:insanium_farmland",
                "agritechevolved:infused_farmland",
                "agritechevolved:mulch",
                "justdirethings:goosoil_tier1",
                "justdirethings:goosoil_tier2",
                "justdirethings:goosoil_tier3",
                "justdirethings:goosoil_tier4",
                "farmersdelight:rich_soil_farmland"
        );
        hexSage.drops = new ArrayList<>();

        DropEntry hexSageDrop = new DropEntry();
        hexSageDrop.item = "hexerei:sage";
        hexSageDrop.count = new CountRange(2, 4);
        hexSageDrop.chance = 1.0f;
        hexSage.drops.add(hexSageDrop);

        DropEntry hexSageSeedDrop = new DropEntry();
        hexSageSeedDrop.item = "hexerei:sage_seed";
        hexSageSeedDrop.count = new CountRange(1, 1);
        hexSageSeedDrop.chance = 0.5f;
        hexSage.drops.add(hexSageSeedDrop);

        crops.add(hexSage);

    }

    private static void addVanillaTrees(List<TreeEntry> trees) {
        // Oak Sapling
        TreeEntry oak = new TreeEntry();
        oak.sapling = "minecraft:oak_sapling";
        oak.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        oak.drops = new ArrayList<>();

        DropEntry oakLogDrop = new DropEntry();
        oakLogDrop.item = "minecraft:oak_log";
        oakLogDrop.count = new CountRange(2, 6);
        oak.drops.add(oakLogDrop);

        DropEntry oakSaplingDrop = new DropEntry();
        oakSaplingDrop.item = "minecraft:oak_sapling";
        oakSaplingDrop.count = new CountRange(1, 2);
        oakSaplingDrop.chance = 0.5f;
        oak.drops.add(oakSaplingDrop);

        DropEntry stickDrop = new DropEntry();
        stickDrop.item = "minecraft:stick";
        stickDrop.count = new CountRange(1, 2);
        stickDrop.chance = 0.5f;
        oak.drops.add(stickDrop);

        DropEntry appleDrop = new DropEntry();
        appleDrop.item = "minecraft:apple";
        appleDrop.count = new CountRange(1, 1);
        appleDrop.chance = 0.4f;
        oak.drops.add(appleDrop);

        trees.add(oak);

        // Birch Sapling
        TreeEntry birch = new TreeEntry();
        birch.sapling = "minecraft:birch_sapling";
        birch.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        birch.drops = new ArrayList<>();

        DropEntry birchLogDrop = new DropEntry();
        birchLogDrop.item = "minecraft:birch_log";
        birchLogDrop.count = new CountRange(2, 6);
        birch.drops.add(birchLogDrop);

        DropEntry birchSaplingDrop = new DropEntry();
        birchSaplingDrop.item = "minecraft:birch_sapling";
        birchSaplingDrop.count = new CountRange(1, 2);
        birchSaplingDrop.chance = 0.5f;
        birch.drops.add(birchSaplingDrop);

        DropEntry birchStickDrop = new DropEntry();
        birchStickDrop.item = "minecraft:stick";
        birchStickDrop.count = new CountRange(1, 2);
        birchStickDrop.chance = 0.5f;
        birch.drops.add(birchStickDrop);

        trees.add(birch);

        // Spruce Sapling
        TreeEntry spruce = new TreeEntry();
        spruce.sapling = "minecraft:spruce_sapling";
        spruce.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        spruce.drops = new ArrayList<>();

        DropEntry spruceLogDrop = new DropEntry();
        spruceLogDrop.item = "minecraft:spruce_log";
        spruceLogDrop.count = new CountRange(4, 8);
        spruce.drops.add(spruceLogDrop);

        DropEntry spruceSaplingDrop = new DropEntry();
        spruceSaplingDrop.item = "minecraft:spruce_sapling";
        spruceSaplingDrop.count = new CountRange(1, 2);
        spruceSaplingDrop.chance = 0.5f;
        spruce.drops.add(spruceSaplingDrop);

        DropEntry spruceStickDrop = new DropEntry();
        spruceStickDrop.item = "minecraft:stick";
        spruceStickDrop.count = new CountRange(1, 2);
        spruceStickDrop.chance = 0.5f;
        spruce.drops.add(spruceStickDrop);

        trees.add(spruce);

        // Jungle Sapling
        TreeEntry jungle = new TreeEntry();
        jungle.sapling = "minecraft:jungle_sapling";
        jungle.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        jungle.drops = new ArrayList<>();

        DropEntry jungleLogDrop = new DropEntry();
        jungleLogDrop.item = "minecraft:jungle_log";
        jungleLogDrop.count = new CountRange(2, 6);
        jungle.drops.add(jungleLogDrop);

        DropEntry jungleSaplingDrop = new DropEntry();
        jungleSaplingDrop.item = "minecraft:jungle_sapling";
        jungleSaplingDrop.count = new CountRange(1, 2);
        jungleSaplingDrop.chance = 0.4f;
        jungle.drops.add(jungleSaplingDrop);

        DropEntry jungleStickDrop = new DropEntry();
        jungleStickDrop.item = "minecraft:stick";
        jungleStickDrop.count = new CountRange(1, 2);
        jungleStickDrop.chance = 0.5f;
        jungle.drops.add(jungleStickDrop);

        DropEntry cocoaBeanDrop = new DropEntry();
        cocoaBeanDrop.item = "minecraft:cocoa_beans";
        cocoaBeanDrop.count = new CountRange(1, 2);
        cocoaBeanDrop.chance = 0.2f;
        jungle.drops.add(cocoaBeanDrop);

        trees.add(jungle);

        // Acacia Sapling
        TreeEntry acacia = new TreeEntry();
        acacia.sapling = "minecraft:acacia_sapling";
        acacia.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        acacia.drops = new ArrayList<>();

        DropEntry acaciaLogDrop = new DropEntry();
        acaciaLogDrop.item = "minecraft:acacia_log";
        acaciaLogDrop.count = new CountRange(2, 6);
        acacia.drops.add(acaciaLogDrop);

        DropEntry acaciaSaplingDrop = new DropEntry();
        acaciaSaplingDrop.item = "minecraft:acacia_sapling";
        acaciaSaplingDrop.count = new CountRange(1, 2);
        acaciaSaplingDrop.chance = 0.5f;
        acacia.drops.add(acaciaSaplingDrop);

        DropEntry acaciaStickDrop = new DropEntry();
        acaciaStickDrop.item = "minecraft:stick";
        acaciaStickDrop.count = new CountRange(1, 2);
        acaciaStickDrop.chance = 0.5f;
        acacia.drops.add(acaciaStickDrop);

        trees.add(acacia);

        // Dark Oak Sapling
        TreeEntry darkOak = new TreeEntry();
        darkOak.sapling = "minecraft:dark_oak_sapling";
        darkOak.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        darkOak.drops = new ArrayList<>();

        DropEntry darkOakLogDrop = new DropEntry();
        darkOakLogDrop.item = "minecraft:dark_oak_log";
        darkOakLogDrop.count = new CountRange(4, 8);
        darkOak.drops.add(darkOakLogDrop);

        DropEntry darkOakSaplingDrop = new DropEntry();
        darkOakSaplingDrop.item = "minecraft:dark_oak_sapling";
        darkOakSaplingDrop.count = new CountRange(1, 2);
        darkOakSaplingDrop.chance = 0.5f;
        darkOak.drops.add(darkOakSaplingDrop);

        DropEntry darkOakStickDrop = new DropEntry();
        darkOakStickDrop.item = "minecraft:stick";
        darkOakStickDrop.count = new CountRange(1, 2);
        darkOakStickDrop.chance = 0.5f;
        darkOak.drops.add(darkOakStickDrop);

        DropEntry appleDarkOakDrop = new DropEntry();
        appleDarkOakDrop.item = "minecraft:apple";
        appleDarkOakDrop.count = new CountRange(1, 2);
        appleDarkOakDrop.chance = 0.3f;
        darkOak.drops.add(appleDarkOakDrop);

        trees.add(darkOak);

        // Mangrove Propagule
        TreeEntry mangrove = new TreeEntry();
        mangrove.sapling = "minecraft:mangrove_propagule";
        mangrove.validSoils = List.of(
                "minecraft:mud",
                "minecraft:muddy_mangrove_roots",
                "minecraft:dirt",
                "minecraft:coarse_dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        mangrove.drops = new ArrayList<>();

        DropEntry mangroveLogDrop = new DropEntry();
        mangroveLogDrop.item = "minecraft:mangrove_log";
        mangroveLogDrop.count = new CountRange(2, 6);
        mangrove.drops.add(mangroveLogDrop);

        DropEntry mangrovePropaguleDrop = new DropEntry();
        mangrovePropaguleDrop.item = "minecraft:mangrove_propagule";
        mangrovePropaguleDrop.count = new CountRange(1, 2);
        mangrovePropaguleDrop.chance = 0.5f;
        mangrove.drops.add(mangrovePropaguleDrop);

        DropEntry mangroveRootsDrop = new DropEntry();
        mangroveRootsDrop.item = "minecraft:mangrove_roots";
        mangroveRootsDrop.count = new CountRange(1, 3);
        mangroveRootsDrop.chance = 0.3f;
        mangrove.drops.add(mangroveRootsDrop);

        DropEntry mangroveStickDrop = new DropEntry();
        mangroveStickDrop.item = "minecraft:mangrove_roots";
        mangroveStickDrop.count = new CountRange(1, 2);
        mangroveStickDrop.chance = 0.5f;
        mangrove.drops.add(mangroveStickDrop);

        trees.add(mangrove);

        // Cherry Sapling
        TreeEntry cherry = new TreeEntry();
        cherry.sapling = "minecraft:cherry_sapling";
        cherry.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        cherry.drops = new ArrayList<>();

        DropEntry cherryLogDrop = new DropEntry();
        cherryLogDrop.item = "minecraft:cherry_log";
        cherryLogDrop.count = new CountRange(2, 6);
        cherry.drops.add(cherryLogDrop);

        DropEntry cherrySaplingDrop = new DropEntry();
        cherrySaplingDrop.item = "minecraft:cherry_sapling";
        cherrySaplingDrop.count = new CountRange(1, 2);
        cherrySaplingDrop.chance = 0.5f;
        cherry.drops.add(cherrySaplingDrop);

        DropEntry cherryStickDrop = new DropEntry();
        cherryStickDrop.item = "minecraft:stick";
        cherryStickDrop.count = new CountRange(1, 2);
        cherryStickDrop.chance = 0.5f;
        cherry.drops.add(cherryStickDrop);

        trees.add(cherry);

        // Azalea
        TreeEntry azalea = new TreeEntry();
        azalea.sapling = "minecraft:azalea";
        azalea.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:rooted_dirt",
                "minecraft:moss_block",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        azalea.drops = new ArrayList<>();

        DropEntry azaleaOakLogDrop = new DropEntry();
        azaleaOakLogDrop.item = "minecraft:oak_log";
        azaleaOakLogDrop.count = new CountRange(2, 6);
        azalea.drops.add(azaleaOakLogDrop);

        DropEntry azaleaDrop = new DropEntry();
        azaleaDrop.item = "minecraft:azalea";
        azaleaDrop.count = new CountRange(1, 1);
        azaleaDrop.chance = 0.5f;
        azalea.drops.add(azaleaDrop);

        DropEntry azaleaStickDrop = new DropEntry();
        azaleaStickDrop.item = "minecraft:stick";
        azaleaStickDrop.count = new CountRange(1, 2);
        azaleaStickDrop.chance = 0.5f;
        azalea.drops.add(azaleaStickDrop);

        DropEntry mossBlockDrop = new DropEntry();
        mossBlockDrop.item = "minecraft:moss_block";
        mossBlockDrop.count = new CountRange(1, 2);
        mossBlockDrop.chance = 0.2f;
        azalea.drops.add(mossBlockDrop);

        trees.add(azalea);

        // Flowering Azalea
        TreeEntry floweringAzalea = new TreeEntry();
        floweringAzalea.sapling = "minecraft:flowering_azalea";
        floweringAzalea.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:rooted_dirt",
                "minecraft:moss_block",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        floweringAzalea.drops = new ArrayList<>();

        DropEntry floweringAzaleaOakLogDrop = new DropEntry();
        floweringAzaleaOakLogDrop.item = "minecraft:oak_log";
        floweringAzaleaOakLogDrop.count = new CountRange(2, 6);
        floweringAzalea.drops.add(floweringAzaleaOakLogDrop);

        DropEntry floweringAzaleaDrop = new DropEntry();
        floweringAzaleaDrop.item = "minecraft:flowering_azalea";
        floweringAzaleaDrop.count = new CountRange(1, 1);
        floweringAzaleaDrop.chance = 0.5f;
        floweringAzalea.drops.add(floweringAzaleaDrop);

        DropEntry floweringAzaleaStickDrop = new DropEntry();
        floweringAzaleaStickDrop.item = "minecraft:stick";
        floweringAzaleaStickDrop.count = new CountRange(1, 2);
        floweringAzaleaStickDrop.chance = 0.5f;
        floweringAzalea.drops.add(floweringAzaleaStickDrop);

        DropEntry floweringAzaleaMossBlockDrop = new DropEntry();
        floweringAzaleaMossBlockDrop.item = "minecraft:moss_block";
        floweringAzaleaMossBlockDrop.count = new CountRange(1, 1);
        floweringAzaleaMossBlockDrop.chance = 0.2f;
        floweringAzalea.drops.add(floweringAzaleaMossBlockDrop);

        trees.add(floweringAzalea);

        // Crimson Stem
        TreeEntry crimson = new TreeEntry();
        crimson.sapling = "minecraft:crimson_fungus";
        crimson.validSoils = List.of(
                "minecraft:crimson_nylium",
                "minecraft:warped_nylium",
                "agritechevolved:mulch"
        );
        crimson.drops = new ArrayList<>();

        DropEntry crimsonLogDrop = new DropEntry();
        crimsonLogDrop.item = "minecraft:crimson_stem";
        crimsonLogDrop.count = new CountRange(2, 6);
        crimson.drops.add(crimsonLogDrop);

        DropEntry crimsonWartDrop = new DropEntry();
        crimsonWartDrop.item = "minecraft:nether_wart_block";
        crimsonWartDrop.count = new CountRange(4, 8);
        crimson.drops.add(crimsonWartDrop);

        DropEntry crimsonVinesDrop = new DropEntry();
        crimsonVinesDrop.item = "minecraft:weeping_vines";
        crimsonVinesDrop.count = new CountRange(1, 2);
        crimson.drops.add(crimsonVinesDrop);

        DropEntry crimsonShroomDrop = new DropEntry();
        crimsonShroomDrop.item = "minecraft:shroomlight";
        crimsonShroomDrop.count = new CountRange(2, 4);
        crimson.drops.add(crimsonShroomDrop);

        trees.add(crimson);

        // Warped Stem
        TreeEntry warped = new TreeEntry();
        warped.sapling = "minecraft:warped_fungus";
        warped.validSoils = List.of(
                "minecraft:crimson_nylium",
                "minecraft:warped_nylium",
                "agritechevolved:mulch"
        );
        warped.drops = new ArrayList<>();

        DropEntry warpedLogDrop = new DropEntry();
        warpedLogDrop.item = "minecraft:warped_stem";
        warpedLogDrop.count = new CountRange(2, 6);
        warped.drops.add(warpedLogDrop);

        DropEntry warpedWartDrop = new DropEntry();
        warpedWartDrop.item = "minecraft:warped_wart_block";
        warpedWartDrop.count = new CountRange(4, 8);
        warped.drops.add(warpedWartDrop);

        DropEntry warpedVinesDrop = new DropEntry();
        warpedVinesDrop.item = "minecraft:twisting_vines";
        warpedVinesDrop.count = new CountRange(1, 2);
        warped.drops.add(warpedVinesDrop);

        DropEntry warpedShroomDrop = new DropEntry();
        warpedShroomDrop.item = "minecraft:shroomlight";
        warpedShroomDrop.count = new CountRange(2, 4);
        warped.drops.add(warpedShroomDrop);

        trees.add(warped);
    }

    private static void addArsElementalTrees(List<TreeEntry> trees) {
        // Yellow Archwood Tree
        TreeEntry yellowArchwood = new TreeEntry();
        yellowArchwood.sapling = "ars_elemental:yellow_archwood_sapling";
        yellowArchwood.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        yellowArchwood.drops = new ArrayList<>();

        DropEntry yellowArchwoodLogDrop = new DropEntry();
        yellowArchwoodLogDrop.item = "ars_elemental:yellow_archwood_log";
        yellowArchwoodLogDrop.count = new CountRange(4, 8);
        yellowArchwood.drops.add(yellowArchwoodLogDrop);

        DropEntry yellowArchwoodSaplingDrop = new DropEntry();
        yellowArchwoodSaplingDrop.item = "ars_elemental:yellow_archwood_sapling";
        yellowArchwoodSaplingDrop.count = new CountRange(1, 1);
        yellowArchwoodSaplingDrop.chance = 0.3f;
        yellowArchwood.drops.add(yellowArchwoodSaplingDrop);

        DropEntry yellowArchwoodPodDrop = new DropEntry();
        yellowArchwoodPodDrop.item = "ars_elemental:flashpine_pod";
        yellowArchwoodPodDrop.count = new CountRange(1, 1);
        yellowArchwoodPodDrop.chance = 0.2f;
        yellowArchwood.drops.add(yellowArchwoodPodDrop);

        DropEntry yellowArchwoodStickDrop = new DropEntry();
        yellowArchwoodStickDrop.item = "minecraft:stick";
        yellowArchwoodStickDrop.count = new CountRange(1, 2);
        yellowArchwoodStickDrop.chance = 0.5f;
        yellowArchwood.drops.add(yellowArchwoodStickDrop);

        trees.add(yellowArchwood);
    }

    private static void addArsNouveauTrees(List<TreeEntry> trees) {
        // Blue Archwood Tree
        TreeEntry blueArchwood = new TreeEntry();
        blueArchwood.sapling = "ars_nouveau:blue_archwood_sapling";
        blueArchwood.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        blueArchwood.drops = new ArrayList<>();

        DropEntry blueArchwoodLogDrop = new DropEntry();
        blueArchwoodLogDrop.item = "ars_nouveau:blue_archwood_log";
        blueArchwoodLogDrop.count = new CountRange(4, 8);
        blueArchwood.drops.add(blueArchwoodLogDrop);

        DropEntry blueArchwoodSaplingDrop = new DropEntry();
        blueArchwoodSaplingDrop.item = "ars_nouveau:blue_archwood_sapling";
        blueArchwoodSaplingDrop.count = new CountRange(1, 1);
        blueArchwoodSaplingDrop.chance = 0.3f;
        blueArchwood.drops.add(blueArchwoodSaplingDrop);

        DropEntry blueArchwoodPodDrop = new DropEntry();
        blueArchwoodPodDrop.item = "ars_nouveau:frostaya_pod";
        blueArchwoodPodDrop.count = new CountRange(1, 1);
        blueArchwoodPodDrop.chance = 0.2f;
        blueArchwood.drops.add(blueArchwoodPodDrop);

        DropEntry blueArchwoodStickDrop = new DropEntry();
        blueArchwoodStickDrop.item = "minecraft:stick";
        blueArchwoodStickDrop.count = new CountRange(1, 2);
        blueArchwoodStickDrop.chance = 0.5f;
        blueArchwood.drops.add(blueArchwoodStickDrop);

        trees.add(blueArchwood);

        // Red Archwood Tree
        TreeEntry redArchwood = new TreeEntry();
        redArchwood.sapling = "ars_nouveau:red_archwood_sapling";
        redArchwood.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        redArchwood.drops = new ArrayList<>();

        DropEntry redArchwoodLogDrop = new DropEntry();
        redArchwoodLogDrop.item = "ars_nouveau:red_archwood_log";
        redArchwoodLogDrop.count = new CountRange(4, 8);
        redArchwood.drops.add(redArchwoodLogDrop);

        DropEntry redArchwoodSaplingDrop = new DropEntry();
        redArchwoodSaplingDrop.item = "ars_nouveau:red_archwood_sapling";
        redArchwoodSaplingDrop.count = new CountRange(1, 1);
        redArchwoodSaplingDrop.chance = 0.3f;
        redArchwood.drops.add(redArchwoodSaplingDrop);

        DropEntry redArchwoodPodDrop = new DropEntry();
        redArchwoodPodDrop.item = "ars_nouveau:bombegranate_pod";
        redArchwoodPodDrop.count = new CountRange(1, 1);
        redArchwoodPodDrop.chance = 0.2f;
        redArchwood.drops.add(redArchwoodPodDrop);

        DropEntry redArchwoodStickDrop = new DropEntry();
        redArchwoodStickDrop.item = "minecraft:stick";
        redArchwoodStickDrop.count = new CountRange(1, 2);
        redArchwoodStickDrop.chance = 0.5f;
        redArchwood.drops.add(redArchwoodStickDrop);

        trees.add(redArchwood);

        // Purple Archwood Tree
        TreeEntry purpleArchwood = new TreeEntry();
        purpleArchwood.sapling = "ars_nouveau:purple_archwood_sapling";
        purpleArchwood.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        purpleArchwood.drops = new ArrayList<>();

        DropEntry purpleArchwoodLogDrop = new DropEntry();
        purpleArchwoodLogDrop.item = "ars_nouveau:purple_archwood_log";
        purpleArchwoodLogDrop.count = new CountRange(4, 8);
        purpleArchwood.drops.add(purpleArchwoodLogDrop);

        DropEntry purpleArchwoodSaplingDrop = new DropEntry();
        purpleArchwoodSaplingDrop.item = "ars_nouveau:purple_archwood_sapling";
        purpleArchwoodSaplingDrop.count = new CountRange(1, 1);
        purpleArchwoodSaplingDrop.chance = 0.3f;
        purpleArchwood.drops.add(purpleArchwoodSaplingDrop);

        DropEntry purpleArchwoodPodDrop = new DropEntry();
        purpleArchwoodPodDrop.item = "ars_nouveau:bastion_pod";
        purpleArchwoodPodDrop.count = new CountRange(1, 1);
        purpleArchwoodPodDrop.chance = 0.2f;
        purpleArchwood.drops.add(purpleArchwoodPodDrop);

        DropEntry purpleArchwoodStickDrop = new DropEntry();
        purpleArchwoodStickDrop.item = "minecraft:stick";
        purpleArchwoodStickDrop.count = new CountRange(1, 2);
        purpleArchwoodStickDrop.chance = 0.5f;
        purpleArchwood.drops.add(purpleArchwoodStickDrop);

        trees.add(purpleArchwood);

        // Green Archwood Tree
        TreeEntry greenArchwood = new TreeEntry();
        greenArchwood.sapling = "ars_nouveau:green_archwood_sapling";
        greenArchwood.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        greenArchwood.drops = new ArrayList<>();

        DropEntry greenArchwoodLogDrop = new DropEntry();
        greenArchwoodLogDrop.item = "ars_nouveau:green_archwood_log";
        greenArchwoodLogDrop.count = new CountRange(4, 8);
        greenArchwood.drops.add(greenArchwoodLogDrop);

        DropEntry greenArchwoodSaplingDrop = new DropEntry();
        greenArchwoodSaplingDrop.item = "ars_nouveau:green_archwood_sapling";
        greenArchwoodSaplingDrop.count = new CountRange(1, 1);
        greenArchwoodSaplingDrop.chance = 0.3f;
        greenArchwood.drops.add(greenArchwoodSaplingDrop);

        DropEntry greenArchwoodPodDrop = new DropEntry();
        greenArchwoodPodDrop.item = "ars_nouveau:mendosteen_pod";
        greenArchwoodPodDrop.count = new CountRange(1, 1);
        greenArchwoodPodDrop.chance = 0.2f;
        greenArchwood.drops.add(greenArchwoodPodDrop);

        DropEntry greenArchwoodStickDrop = new DropEntry();
        greenArchwoodStickDrop.item = "minecraft:stick";
        greenArchwoodStickDrop.count = new CountRange(1, 2);
        greenArchwoodStickDrop.chance = 0.5f;
        greenArchwood.drops.add(greenArchwoodStickDrop);

        trees.add(greenArchwood);
    }

    private static void addEvilCraftTrees(List<TreeEntry> trees) {
        // Undead Tree
        TreeEntry undead = new TreeEntry();
        undead.sapling = "evilcraft:undead_sapling";
        undead.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        undead.drops = new ArrayList<>();

        DropEntry undeadLogDrop = new DropEntry();
        undeadLogDrop.item = "evilcraft:undead_log";
        undeadLogDrop.count = new CountRange(2, 6);
        undead.drops.add(undeadLogDrop);

        DropEntry undeadDeadbushDrop = new DropEntry();
        undeadDeadbushDrop.item = "minecraft:dead_bush";
        undeadDeadbushDrop.count = new CountRange(1, 2);
        undead.drops.add(undeadDeadbushDrop);

        trees.add(undead);
    }

    private static void addForbiddenArcanusTrees(List<TreeEntry> trees) {
        // Forbidden Arcanus
        TreeEntry aurum = new TreeEntry();
        aurum.sapling = "forbidden_arcanus:aurum_sapling";
        aurum.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        aurum.drops = new ArrayList<>();

        DropEntry aurumLogDrop = new DropEntry();
        aurumLogDrop.item = "forbidden_arcanus:aurum_log";
        aurumLogDrop.count = new CountRange(2, 6);
        aurum.drops.add(aurumLogDrop);

        DropEntry aurumSaplingDrop = new DropEntry();
        aurumSaplingDrop.item = "forbidden_arcanus:aurum_sapling";
        aurumSaplingDrop.count = new CountRange(1, 3);
        aurum.drops.add(aurumSaplingDrop);

        DropEntry aurumStickDrop = new DropEntry();
        aurumStickDrop.item = "minecraft:stick";
        aurumStickDrop.count = new CountRange(1, 2);
        aurumStickDrop.chance = 0.5f;
        aurum.drops.add(aurumStickDrop);

        DropEntry aurumNuggetDrop = new DropEntry();
        aurumNuggetDrop.item = "minecraft:gold_nugget";
        aurumNuggetDrop.count = new CountRange(1, 2);
        aurumNuggetDrop.chance = 0.1f;
        aurum.drops.add(aurumNuggetDrop);

        trees.add(aurum);

        // Edelwood
        TreeEntry edelwood = new TreeEntry();
        edelwood.sapling = "forbidden_arcanus:growing_edelwood";
        edelwood.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        edelwood.drops = new ArrayList<>();

        DropEntry edelwoodLogDrop = new DropEntry();
        edelwoodLogDrop.item = "forbidden_arcanus:edelwood_log";
        edelwoodLogDrop.count = new CountRange(2, 6);
        edelwood.drops.add(edelwoodLogDrop);

        DropEntry edelwoodCarvedLogDrop = new DropEntry();
        edelwoodCarvedLogDrop.item = "forbidden_arcanus:carved_edelwood_log";
        edelwoodCarvedLogDrop.count = new CountRange(1, 1);
        aurumNuggetDrop.chance = 0.4f;
        edelwood.drops.add(edelwoodCarvedLogDrop);

        trees.add(edelwood);
    }

    private static void addIntegratedDynamicsTrees(List<TreeEntry> trees) {
        // Menril Trees
        TreeEntry menril = new TreeEntry();
        menril.sapling = "integrateddynamics:menril_sapling";
        menril.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        menril.drops = new ArrayList<>();

        DropEntry menrilLogDrop = new DropEntry();
        menrilLogDrop.item = "integrateddynamics:menril_log";
        menrilLogDrop.count = new CountRange(2, 6);
        menril.drops.add(menrilLogDrop);

        DropEntry menrilSaplingDrop = new DropEntry();
        menrilSaplingDrop.item = "integrateddynamics:menril_sapling";
        menrilSaplingDrop.count = new CountRange(1, 2);
        menril.drops.add(menrilSaplingDrop);

        DropEntry menrilChunkDrop = new DropEntry();
        menrilChunkDrop.item = "integrateddynamics:crystalized_menril_chunk";
        menrilChunkDrop.count = new CountRange(1, 2);
        menrilChunkDrop.chance = 0.5f;
        menril.drops.add(menrilChunkDrop);

        DropEntry menrilBerriesDrop = new DropEntry();
        menrilBerriesDrop.item = "integrateddynamics:menril_berries";
        menrilBerriesDrop.count = new CountRange(2, 4);
        menrilBerriesDrop.chance = 0.5f;
        menril.drops.add(menrilBerriesDrop);

        DropEntry menrilStickDrop = new DropEntry();
        menrilStickDrop.item = "minecraft:stick";
        menrilStickDrop.count = new CountRange(1, 2);
        menrilStickDrop.chance = 0.5f;
        menril.drops.add(menrilStickDrop);

        trees.add(menril);
    }

    private static void addHexereiTrees(List<TreeEntry> trees) {
        // Mahogany
        TreeEntry mahogany = new TreeEntry();
        mahogany.sapling = "hexerei:mahogany_sapling";
        mahogany.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        mahogany.drops = new ArrayList<>();

        DropEntry mahoganyLogDrop = new DropEntry();
        mahoganyLogDrop.item = "hexerei:mahogany_log";
        mahoganyLogDrop.count = new CountRange(4, 8);
        mahogany.drops.add(mahoganyLogDrop);

        DropEntry mahoganySaplingDrop = new DropEntry();
        mahoganySaplingDrop.item = "hexerei:mahogany_sapling";
        mahoganySaplingDrop.count = new CountRange(1, 1);
        mahogany.drops.add(mahoganySaplingDrop);

        DropEntry mahoganyStickDrop = new DropEntry();
        mahoganyStickDrop.item = "minecraft:stick";
        mahoganyStickDrop.count = new CountRange(1, 2);
        mahoganyStickDrop.chance = 0.5f;
        mahogany.drops.add(mahoganyStickDrop);

        trees.add(mahogany);

        // Willow
        TreeEntry willow = new TreeEntry();
        willow.sapling = "hexerei:willow_sapling";
        willow.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        willow.drops = new ArrayList<>();

        DropEntry willowLogDrop = new DropEntry();
        willowLogDrop.item = "hexerei:willow_log";
        willowLogDrop.count = new CountRange(4, 8);
        willow.drops.add(willowLogDrop);

        DropEntry willowSaplingDrop = new DropEntry();
        willowSaplingDrop.item = "hexerei:willow_sapling";
        willowSaplingDrop.count = new CountRange(1, 1);
        willow.drops.add(willowSaplingDrop);

        DropEntry willowStickDrop = new DropEntry();
        willowStickDrop.item = "minecraft:stick";
        willowStickDrop.count = new CountRange(1, 2);
        willowStickDrop.chance = 0.5f;
        willow.drops.add(willowStickDrop);

        trees.add(willow);

        // Witch Hazel
        TreeEntry witchHazel = new TreeEntry();
        witchHazel.sapling = "hexerei:witch_hazel_sapling";
        witchHazel.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        witchHazel.drops = new ArrayList<>();

        DropEntry witchHazelLogDrop = new DropEntry();
        witchHazelLogDrop.item = "hexerei:witch_hazel_log";
        witchHazelLogDrop.count = new CountRange(2, 6);
        witchHazel.drops.add(witchHazelLogDrop);

        DropEntry witchHazelSaplingDrop = new DropEntry();
        witchHazelSaplingDrop.item = "hexerei:witch_hazel_sapling";
        witchHazelSaplingDrop.count = new CountRange(1, 1);
        witchHazel.drops.add(witchHazelSaplingDrop);

        DropEntry witchHazelStickDrop = new DropEntry();
        witchHazelStickDrop.item = "minecraft:stick";
        witchHazelStickDrop.count = new CountRange(1, 2);
        witchHazelStickDrop.chance = 0.5f;
        witchHazel.drops.add(witchHazelStickDrop);

        trees.add(witchHazel);
    }

    private static void addOccultismTrees(List<TreeEntry> trees) {
        // Otherworld
        TreeEntry otherworld = new TreeEntry();
        otherworld.sapling = "occultism:otherworld_sapling";
        otherworld.validSoils = List.of(
                "minecraft:dirt",
                "minecraft:grass_block",
                "minecraft:podzol",
                "minecraft:coarse_dirt",
                "minecraft:mycelium",
                "agritechevolved:mulch"
        );
        otherworld.drops = new ArrayList<>();

        DropEntry otherworldLogDrop = new DropEntry();
        otherworldLogDrop.item = "occultism:otherworld_log";
        otherworldLogDrop.count = new CountRange(2, 6);
        otherworld.drops.add(otherworldLogDrop);

        DropEntry otherworldSaplingDrop = new DropEntry();
        otherworldSaplingDrop.item = "occultism:otherworld_sapling";
        otherworldSaplingDrop.count = new CountRange(1, 3);
        otherworld.drops.add(otherworldSaplingDrop);

        trees.add(otherworld);
    }

    private static void addVanillaSoils(List<SoilEntry> soils) {
        SoilEntry dirt = new SoilEntry();
        dirt.soil = "minecraft:dirt";
        dirt.growthModifier = 0.475f;
        soils.add(dirt);

        SoilEntry coarseDirt = new SoilEntry();
        coarseDirt.soil = "minecraft:coarse_dirt";
        coarseDirt.growthModifier = 0.475f;
        soils.add(coarseDirt);

        SoilEntry podzol = new SoilEntry();
        podzol.soil = "minecraft:podzol";
        podzol.growthModifier = 0.475f;
        soils.add(podzol);

        SoilEntry mycelium = new SoilEntry();
        mycelium.soil = "minecraft:mycelium";
        mycelium.growthModifier = 0.475f;
        soils.add(mycelium);

        SoilEntry mud = new SoilEntry();
        mud.soil = "minecraft:mud";
        mud.growthModifier = 0.5f;
        soils.add(mud);

        SoilEntry muddyMangroveRoots = new SoilEntry();
        muddyMangroveRoots.soil = "minecraft:muddy_mangrove_roots";
        muddyMangroveRoots.growthModifier = 0.5f;
        soils.add(muddyMangroveRoots);

        SoilEntry rootedDirt = new SoilEntry();
        rootedDirt.soil = "minecraft:rooted_dirt";
        rootedDirt.growthModifier = 0.475f;
        soils.add(rootedDirt);

        SoilEntry moss = new SoilEntry();
        moss.soil = "minecraft:moss_block";
        moss.growthModifier = 0.475f;
        soils.add(moss);

        SoilEntry farmland = new SoilEntry();
        farmland.soil = "minecraft:farmland";
        farmland.growthModifier = 0.5f;
        soils.add(farmland);

        SoilEntry sand = new SoilEntry();
        sand.soil = "minecraft:sand";
        sand.growthModifier = 0.5f;
        soils.add(sand);

        SoilEntry redSand = new SoilEntry();
        redSand.soil = "minecraft:red_sand";
        redSand.growthModifier = 0.5f;
        soils.add(redSand);

        SoilEntry grass = new SoilEntry();
        grass.soil = "minecraft:grass_block";
        grass.growthModifier = 0.475f;
        soils.add(grass);

        SoilEntry soulSand = new SoilEntry();
        soulSand.soil = "minecraft:soul_sand";
        soulSand.growthModifier = 0.5f;
        soils.add(soulSand);

        SoilEntry endStone = new SoilEntry();
        endStone.soil = "minecraft:end_stone";
        endStone.growthModifier = 0.5f;
        soils.add(endStone);

        SoilEntry jungleLog = new SoilEntry();
        jungleLog.soil = "minecraft:jungle_log";
        jungleLog.growthModifier = 0.5f;
        soils.add(jungleLog);

        SoilEntry jungleWood = new SoilEntry();
        jungleWood.soil = "minecraft:jungle_wood";
        jungleWood.growthModifier = 0.5f;
        soils.add(jungleWood);

        SoilEntry strippedJungleLog = new SoilEntry();
        strippedJungleLog.soil = "minecraft:stripped_jungle_log";
        strippedJungleLog.growthModifier = 0.5f;
        soils.add(strippedJungleLog);

        SoilEntry strippedJungleWood = new SoilEntry();
        strippedJungleWood.soil = "minecraft:stripped_jungle_wood";
        strippedJungleWood.growthModifier = 0.5f;
        soils.add(strippedJungleWood);
    }

    private static void addAgritechEvolvedSoils(List<SoilEntry> soils) {
        SoilEntry infusedFarmland = new SoilEntry();
        infusedFarmland.soil = "agritechevolved:infused_farmland";
        infusedFarmland.growthModifier = 1.5f;
        soils.add(infusedFarmland);

        SoilEntry agritechEvolvedMulch = new SoilEntry();
        agritechEvolvedMulch.soil = "agritechevolved:mulch";
        agritechEvolvedMulch.growthModifier = 2.0f;
        soils.add(agritechEvolvedMulch);
    }

    private static void addFarmersDelightSoils(List<SoilEntry> soils) {
        SoilEntry richSoil = new SoilEntry();
        richSoil.soil = "farmersdelight:rich_soil";
        richSoil.growthModifier = 0.525f;
        soils.add(richSoil);

        SoilEntry richSoilFarmland = new SoilEntry();
        richSoilFarmland.soil = "farmersdelight:rich_soil_farmland";
        richSoilFarmland.growthModifier = 0.525f;
        soils.add(richSoilFarmland);

        SoilEntry organicCompost = new SoilEntry();
        organicCompost.soil = "farmersdelight:organic_compost";
        organicCompost.growthModifier = 0.525f;
        soils.add(organicCompost);
    }

    private static void addMysticalAgricultureSoils(List<SoilEntry> soils) {
        // Add MA soils with appropriate growth modifiers
        SoilEntry inferiumFarmland = new SoilEntry();
        inferiumFarmland.soil = "mysticalagriculture:inferium_farmland";
        inferiumFarmland.growthModifier = 0.55f;
        soils.add(inferiumFarmland);

        SoilEntry prudentiumFarmland = new SoilEntry();
        prudentiumFarmland.soil = "mysticalagriculture:prudentium_farmland";
        prudentiumFarmland.growthModifier = 0.625f;
        soils.add(prudentiumFarmland);

        SoilEntry tertiumFarmland = new SoilEntry();
        tertiumFarmland.soil = "mysticalagriculture:tertium_farmland";
        tertiumFarmland.growthModifier = 0.75f;
        soils.add(tertiumFarmland);

        SoilEntry imperiumFarmland = new SoilEntry();
        imperiumFarmland.soil = "mysticalagriculture:imperium_farmland";
        imperiumFarmland.growthModifier = 0.875f;
        soils.add(imperiumFarmland);

        SoilEntry supremiumFarmland = new SoilEntry();
        supremiumFarmland.soil = "mysticalagriculture:supremium_farmland";
        supremiumFarmland.growthModifier = 1f;
        soils.add(supremiumFarmland);

        if (Config.enableMysticalAgradditions) {
            SoilEntry insaniumFarmland = new SoilEntry();
            insaniumFarmland.soil = "mysticalagradditions:insanium_farmland";
            insaniumFarmland.growthModifier = 1.75f;
            soils.add(insaniumFarmland);

            SoilEntry netherStarCrux = new SoilEntry();
            netherStarCrux.soil = "mysticalagradditions:nether_star_crux";
            netherStarCrux.growthModifier = 1.75f;
            soils.add(netherStarCrux);

            SoilEntry dragonEggCrux = new SoilEntry();
            dragonEggCrux.soil = "mysticalagradditions:dragon_egg_crux";
            dragonEggCrux.growthModifier = 1.75f;
            soils.add(dragonEggCrux);

            SoilEntry awakenedDraconiumCrux = new SoilEntry();
            awakenedDraconiumCrux.soil = "mysticalagradditions:awakened_draconium_crux";
            awakenedDraconiumCrux.growthModifier = 1.75f;
            soils.add(awakenedDraconiumCrux);

            SoilEntry neutroniumCrux = new SoilEntry();
            neutroniumCrux.soil = "mysticalagradditions:neutronium_crux";
            neutroniumCrux.growthModifier = 1.75f;
            soils.add(neutroniumCrux);

            SoilEntry nitroCrystalCrux = new SoilEntry();
            nitroCrystalCrux.soil = "mysticalagradditions:nitro_crystal_crux";
            nitroCrystalCrux.growthModifier = 1.75f;
            soils.add(nitroCrystalCrux);
        }
    }

    private static void addJustDireThingsSoils(List<SoilEntry> soils) {
        SoilEntry goosoilTier1 = new SoilEntry();
        goosoilTier1.soil = "justdirethings:goosoil_tier1";
        goosoilTier1.growthModifier = 0.575f;
        soils.add(goosoilTier1);

        SoilEntry goosoilTier2 = new SoilEntry();
        goosoilTier2.soil = "justdirethings:goosoil_tier2";
        goosoilTier2.growthModifier = 0.75f;
        soils.add(goosoilTier2);

        SoilEntry goosoilTier3 = new SoilEntry();
        goosoilTier3.soil = "justdirethings:goosoil_tier3";
        goosoilTier3.growthModifier = 1.0f;
        soils.add(goosoilTier3);

        SoilEntry goosoilTier4 = new SoilEntry();
        goosoilTier4.soil = "justdirethings:goosoil_tier4";
        goosoilTier4.growthModifier = 1.5f;
        soils.add(goosoilTier4);
    }

    private static void addVanillaFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry boneMeal = new FertilizerEntry();
        boneMeal.item = "minecraft:bone_meal";
        boneMeal.speedMultiplier = 1.2f;
        boneMeal.yieldMultiplier = 1.2f;
        fertilizers.add(boneMeal);

        FertilizerEntry biomass = new FertilizerEntry();
        biomass.item = "agritechevolved:biomass";
        biomass.speedMultiplier = 1.2f;
        biomass.yieldMultiplier = 1.2f;
        fertilizers.add(biomass);
    }

    private static void addAgritechEvolvedFertilizer(List<FertilizerEntry> fertilizers) {
        FertilizerEntry biomass = new FertilizerEntry();
        biomass.item = "agritechevolved:biomass";
        biomass.speedMultiplier = 1.3f;
        biomass.yieldMultiplier = 1.3f;
        fertilizers.add(biomass);

        FertilizerEntry compactedBiomass = new FertilizerEntry();
        compactedBiomass.item = "agritechevolved:compacted_biomass";
        compactedBiomass.speedMultiplier = 1.8f;
        compactedBiomass.yieldMultiplier = 1.8f;
        fertilizers.add(compactedBiomass);
    }

    private static void addImmersiveEngineeringFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry fertilizer = new FertilizerEntry();
        fertilizer.item = "immersiveengineering:fertilizer";
        fertilizer.speedMultiplier = 1.4f;
        fertilizer.yieldMultiplier = 1.4f;
        fertilizers.add(fertilizer);
    }

    private static void addForbiddenArcanusFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry arcaneBoneMeal = new FertilizerEntry();
        arcaneBoneMeal.item = "forbidden_arcanus:arcane_bone_meal";
        arcaneBoneMeal.speedMultiplier = 1.5f;
        arcaneBoneMeal.yieldMultiplier = 1.5f;
        fertilizers.add(arcaneBoneMeal);
    }

    private static void addMysticalAgricultureFertilizers(List<FertilizerEntry> fertilizers) {
        FertilizerEntry mysticalFertilizer = new FertilizerEntry();
        mysticalFertilizer.item = "mysticalagriculture:mystical_fertilizer";
        mysticalFertilizer.speedMultiplier = 1.6f;
        mysticalFertilizer.yieldMultiplier = 1.6f;
        fertilizers.add(mysticalFertilizer);

        FertilizerEntry fertilizerEssence = new FertilizerEntry();
        fertilizerEssence.item = "mysticalagriculture:fertilized_essence";
        fertilizerEssence.speedMultiplier = 1.3f;
        fertilizerEssence.yieldMultiplier = 1.3f;
        fertilizers.add(fertilizerEssence);
    }

    private static void processConfig(PlantablesConfigData configData) {
        crops.clear();
        trees.clear();
        soils.clear();

        if (configData.allowedCrops != null) {
            for (CropEntry entry : configData.allowedCrops) {
                if (entry.seed != null && !entry.seed.isEmpty()) {
                    CropInfo cropInfo = createPlantInfo(entry.validSoils, entry.soil, entry.drops);
                    crops.put(entry.seed, cropInfo);
                }
            }
        }

        if (configData.allowedTrees != null) {
            for (TreeEntry entry : configData.allowedTrees) {
                if (entry.sapling != null && !entry.sapling.isEmpty()) {
                    TreeInfo treeInfo = createTreeInfo(entry.validSoils, entry.soil, entry.drops);
                    trees.put(entry.sapling, treeInfo);
                }
            }
        }

        if (configData.allowedSoils != null) {
            for (SoilEntry entry : configData.allowedSoils) {
                if (entry.soil != null && !entry.soil.isEmpty()) {
                    soils.put(entry.soil, new SoilInfo(entry.growthModifier));
                }
            }
        }

        if (configData.allowedFertilizers != null) {
            for (FertilizerEntry entry : configData.allowedFertilizers) {
                if (entry.item != null && !entry.item.isEmpty()) {
                    fertilizers.put(entry.item, new FertilizerInfo(entry.speedMultiplier, entry.yieldMultiplier));
                }
            }
        }

        LOGGER.info("Loaded {} crops, {} trees, and {} soils from config", crops.size(), trees.size(), soils.size());
    }

    private static CropInfo createPlantInfo(List<String> validSoils, String soil, List<DropEntry> drops) {
        CropInfo plantInfo = new CropInfo();
        plantInfo.drops = new ArrayList<>();

        if (validSoils != null && !validSoils.isEmpty()) {
            plantInfo.validSoils.addAll(validSoils);
        } else if (soil != null && !soil.isEmpty()) {
            plantInfo.validSoils.add(soil);
        }

        if (drops != null) {
            for (DropEntry dropEntry : drops) {
                DropInfo dropInfo = new DropInfo(
                        dropEntry.item,
                        dropEntry.count != null ? dropEntry.count.min : 1,
                        dropEntry.count != null ? dropEntry.count.max : 1,
                        dropEntry.chance
                );
                plantInfo.drops.add(dropInfo);
            }
        }

        return plantInfo;
    }

    private static TreeInfo createTreeInfo(List<String> validSoils, String soil, List<DropEntry> drops) {
        TreeInfo treeInfo = new TreeInfo();
        treeInfo.drops = new ArrayList<>();

        if (validSoils != null && !validSoils.isEmpty()) {
            treeInfo.validSoils.addAll(validSoils);
        } else if (soil != null && !soil.isEmpty()) {
            treeInfo.validSoils.add(soil);
        }

        if (drops != null) {
            for (DropEntry dropEntry : drops) {
                DropInfo dropInfo = new DropInfo(
                        dropEntry.item,
                        dropEntry.count != null ? dropEntry.count.min : 1,
                        dropEntry.count != null ? dropEntry.count.max : 1,
                        dropEntry.chance
                );
                treeInfo.drops.add(dropInfo);
            }
        }

        return treeInfo;
    }

    public static boolean isSoilValidForSeed(String soilId, String seedId) {
        CropInfo cropInfo = crops.get(seedId);
        if (cropInfo == null || cropInfo.validSoils.isEmpty()) {
            return false;
        }
        return cropInfo.validSoils.contains(soilId);
    }

    public static boolean isValidSeed(String itemId) {
        return crops.containsKey(itemId);
    }

    public static List<DropInfo> getCropDrops(String seedId) {
        CropInfo info = crops.get(seedId);
        return info != null ? info.drops : Collections.emptyList();
    }

    public static Map<String, List<String>> getAllSeedToSoilMappings() {
        Map<String, List<String>> seedToSoilMap = new HashMap<>();
        for (Map.Entry<String, CropInfo> entry : crops.entrySet()) {
            String seedId = entry.getKey();
            CropInfo cropInfo = entry.getValue();
            if (!cropInfo.validSoils.isEmpty()) {
                seedToSoilMap.put(seedId, new ArrayList<>(cropInfo.validSoils));
            }
        }
        return seedToSoilMap;
    }

    public static boolean isSoilValidForSapling(String soilId, String saplingId) {
        TreeInfo treeInfo = trees.get(saplingId);
        if (treeInfo == null || treeInfo.validSoils.isEmpty()) {
            return false;
        }
        return treeInfo.validSoils.contains(soilId);
    }

    public static boolean isValidSapling(String itemId) {
        return trees.containsKey(itemId);
    }

    public static List<DropInfo> getTreeDrops(String saplingId) {
        TreeInfo info = trees.get(saplingId);
        return info != null ? info.drops : Collections.emptyList();
    }

    public static int getBaseSaplingGrowthTime(String saplingId) {
        return 2000;
    }

    public static Map<String, List<String>> getAllSaplingToSoilMappings() {
        Map<String, List<String>> saplingToSoilMap = new HashMap<>();
        for (Map.Entry<String, TreeInfo> entry : trees.entrySet()) {
            String saplingId = entry.getKey();
            TreeInfo treeInfo = entry.getValue();
            if (!treeInfo.validSoils.isEmpty()) {
                saplingToSoilMap.put(saplingId, new ArrayList<>(treeInfo.validSoils));
            }
        }
        return saplingToSoilMap;
    }

    public static boolean isValidSoil(String blockId) {
        return soils.containsKey(blockId);
    }

    public static boolean isValidFertilizer(String itemId) {
        return fertilizers.containsKey(itemId);
    }

    public static FertilizerInfo getFertilizerInfo(String itemId) {
        return fertilizers.get(itemId);
    }

    public static float getSoilGrowthModifier(String blockId) {
        SoilInfo info = soils.get(blockId);
        return info != null ? info.growthModifier : 1.0f;
    }

    public static class PlantablesConfigData {
        public List<CropEntry> allowedCrops;
        public List<TreeEntry> allowedTrees;
        public List<SoilEntry> allowedSoils;
        public List<FertilizerEntry> allowedFertilizers;
    }

    public static class CropEntry {
        public String seed;
        public String soil;
        public List<String> validSoils;
        public List<DropEntry> drops;
    }

    public static class TreeEntry {
        public String sapling;
        public String soil;
        public List<String> validSoils;
        public List<DropEntry> drops;
    }

    public static class DropEntry {
        public String item;
        public CountRange count;
        public float chance = 1.0f;
    }

    public static class FertilizerEntry {
        public String item;
        public float speedMultiplier = 1.2f;
        public float yieldMultiplier = 1.2f;
    }

    public static class FertilizerInfo {
        public final float speedMultiplier;
        public final float yieldMultiplier;

        public FertilizerInfo(float speedMultiplier, float yieldMultiplier) {
            this.speedMultiplier = speedMultiplier;
            this.yieldMultiplier = yieldMultiplier;
        }
    }

    public static class CountRange {
        public int min;
        public int max;

        public CountRange() {
            this.min = 1;
            this.max = 1;
        }

        public CountRange(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    public static class SoilEntry {
        public String soil;
        public float growthModifier;
    }

    public static class CropInfo {
        public List<DropInfo> drops;
        public List<String> validSoils = new ArrayList<>();
    }

    public static class TreeInfo {
        public List<DropInfo> drops;
        public List<String> validSoils = new ArrayList<>();
    }

    public static class DropInfo {
        public final String item;
        public final int minCount;
        public final int maxCount;
        public final float chance;

        public DropInfo(String item, int minCount, int maxCount, float chance) {
            this.item = item;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.chance = chance;
        }
    }

    public static class SoilInfo {
        public final float growthModifier;

        public SoilInfo(float growthModifier) {
            this.growthModifier = growthModifier;
        }
    }
}
