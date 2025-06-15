package com.blocklogic.agritechevolved;

import com.blocklogic.agritechevolved.config.CompostableConfig;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Config {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec COMMON_CONFIG;

    // Legacy compatibility field for existing registration
    public static ModConfigSpec SPEC;

    // ========================================
    // CATEGORY CONSTANTS
    // ========================================

    public static final String CATEGORY_COMPATIBILITY = "compatibility";
    public static final String CATEGORY_MODULES = "modules";
    public static final String CATEGORY_MACHINES = "machines";
    public static final String CATEGORY_PLANTER = "advanced_planter";
    public static final String CATEGORY_COMPOSTER = "composter";
    public static final String CATEGORY_BURNER = "burner";
    public static final String CATEGORY_CAPACITORS = "capacitors";

    // ========================================
    // COMPATIBILITY CONFIGURATION
    // ========================================

    public static ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRICULTURE;
    public static ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRADDITIONS;
    public static ModConfigSpec.BooleanValue ENABLE_FARMERS_DELIGHT;
    public static ModConfigSpec.BooleanValue ENABLE_ARS_NOUVEAU;
    public static ModConfigSpec.BooleanValue ENABLE_ARS_ELEMENTAL;
    public static ModConfigSpec.BooleanValue ENABLE_SILENT_GEAR;
    public static ModConfigSpec.BooleanValue ENABLE_JUST_DIRE_THINGS;
    public static ModConfigSpec.BooleanValue ENABLE_IMMERSIVE_ENGINEERING;
    public static ModConfigSpec.BooleanValue ENABLE_EVILCRAFT;
    public static ModConfigSpec.BooleanValue ENABLE_FORBIDDEN_ARCANUS;
    public static ModConfigSpec.BooleanValue ENABLE_INTEGRATED_DYNAMICS;
    public static ModConfigSpec.BooleanValue ENABLE_OCCULTISM;
    public static ModConfigSpec.BooleanValue ENABLE_ATE;

    // ========================================
    // MODULE CONFIGURATION
    // ========================================

    // Speed Modules
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK1_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK1_POWER_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK2_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK2_POWER_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK3_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SPEED_MODULE_MK3_POWER_MULTIPLIER;

    // Yield Modules
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK1_MULTIPLIER;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK1_SPEED_PENALTY;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK2_MULTIPLIER;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK2_SPEED_PENALTY;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK3_MULTIPLIER;
    public static ModConfigSpec.DoubleValue YIELD_MODULE_MK3_SPEED_PENALTY;

    // ========================================
    // MACHINE CONFIGURATION
    // ========================================

    // Advanced Planter
    public static ModConfigSpec.IntValue PLANTER_BASE_POWER_CONSUMPTION;
    public static ModConfigSpec.IntValue PLANTER_BASE_PROCESSING_TIME;
    public static ModConfigSpec.IntValue PLANTER_ENERGY_BUFFER;

    // Composter
    public static ModConfigSpec.IntValue COMPOSTER_BASE_POWER_CONSUMPTION;
    public static ModConfigSpec.IntValue COMPOSTER_BASE_PROCESSING_TIME;
    public static ModConfigSpec.IntValue COMPOSTER_ENERGY_BUFFER;
    public static ModConfigSpec.IntValue COMPOSTER_ITEMS_PER_BIOMASS;

    // Burner
    public static ModConfigSpec.IntValue BURNER_ENERGY_BUFFER;
    public static ModConfigSpec.IntValue BURNER_BIOMASS_RF_VALUE;
    public static ModConfigSpec.IntValue BURNER_COMPACTED_BIOMASS_RF_VALUE;
    public static ModConfigSpec.IntValue BURNER_CRUDE_BIOMASS_RF_VALUE;

    // Capacitors
    public static ModConfigSpec.IntValue CAPACITOR_T1_BUFFER;
    public static ModConfigSpec.IntValue CAPACITOR_T1_TRANSFER_RATE;
    public static ModConfigSpec.IntValue CAPACITOR_T2_BUFFER;
    public static ModConfigSpec.IntValue CAPACITOR_T2_TRANSFER_RATE;
    public static ModConfigSpec.IntValue CAPACITOR_T3_BUFFER;
    public static ModConfigSpec.IntValue CAPACITOR_T3_TRANSFER_RATE;

    // ========================================
    // STATIC CACHED VALUES
    // ========================================

    public static boolean enableMysticalAgriculture;
    public static boolean enableMysticalAgradditions;
    public static boolean enableFarmersDelight;
    public static boolean enableArsNouveau;
    public static boolean enableArsElemental;
    public static boolean enableSilentGear;
    public static boolean enableJustDireThings;
    public static boolean enableImmersiveEngineering;
    public static boolean enableEvilCraft;
    public static boolean enableForbiddenArcanus;
    public static boolean enableIntegratedDynamics;
    public static boolean enableOccultism;
    public static boolean enableAgritechEvolved;

    // ========================================
    // REGISTRATION METHODS
    // ========================================

    public static void register(ModContainer container) {
        registerCommonConfigs(container);
    }

    private static void registerCommonConfigs(ModContainer container) {
        compatibilityConfig();
        moduleConfig();
        machineConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
        SPEC = COMMON_CONFIG; // Legacy compatibility
        container.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    // ========================================
    // CONFIGURATION CATEGORY METHODS
    // ========================================

    private static void compatibilityConfig() {
        COMMON_BUILDER.comment("Mod Compatibility Settings").push(CATEGORY_COMPATIBILITY);

        ENABLE_MYSTICAL_AGRICULTURE = COMMON_BUILDER.comment("Enable Mystical Agriculture compatibility")
                .define("enable_mystical_agriculture", true);
        ENABLE_MYSTICAL_AGRADDITIONS = COMMON_BUILDER.comment("Enable Mystical Agradditions compatibility")
                .define("enable_mystical_agradditions", true);
        ENABLE_FARMERS_DELIGHT = COMMON_BUILDER.comment("Enable Farmer's Delight compatibility")
                .define("enable_farmers_delight", true);
        ENABLE_ARS_NOUVEAU = COMMON_BUILDER.comment("Enable Ars Nouveau compatibility")
                .define("enable_ars_nouveau", true);
        ENABLE_ARS_ELEMENTAL = COMMON_BUILDER.comment("Enable Ars Elemental compatibility")
                .define("enable_ars_elemental", true);
        ENABLE_SILENT_GEAR = COMMON_BUILDER.comment("Enable Silent Gear compatibility")
                .define("enable_silent_gear", true);
        ENABLE_JUST_DIRE_THINGS = COMMON_BUILDER.comment("Enable Just Dire Things compatibility")
                .define("enable_just_dire_things", true);
        ENABLE_IMMERSIVE_ENGINEERING = COMMON_BUILDER.comment("Enable Immersive Engineering compatibility")
                .define("enable_immersive_engineering", true);
        ENABLE_EVILCRAFT = COMMON_BUILDER.comment("Enable EvilCraft compatibility")
                .define("enable_evilcraft", true);
        ENABLE_FORBIDDEN_ARCANUS = COMMON_BUILDER.comment("Enable Forbidden and Arcanus compatibility")
                .define("enable_forbidden_arcanus", true);
        ENABLE_INTEGRATED_DYNAMICS = COMMON_BUILDER.comment("Enable Integrated Dynamics compatibility")
                .define("enable_integrated_dynamics", true);
        ENABLE_OCCULTISM = COMMON_BUILDER.comment("Enable Occultism compatibility")
                .define("enable_occultism", true);
        ENABLE_ATE = COMMON_BUILDER.comment("Enable AgriTech: Evolved internal content")
                .define("enable_agritech_evolved", true);

        COMMON_BUILDER.pop();
    }

    private static void moduleConfig() {
        COMMON_BUILDER.comment("Module Effectiveness Settings").push(CATEGORY_MODULES);

        // Speed Modules
        COMMON_BUILDER.comment("Speed Module Configuration").push("speed_modules");
        SPEED_MODULE_MK1_MULTIPLIER = COMMON_BUILDER.comment("Speed multiplier for SM-MK1 module")
                .defineInRange("mk1_speed_multiplier", 1.1, 0.1, 10.0);
        SPEED_MODULE_MK1_POWER_MULTIPLIER = COMMON_BUILDER.comment("Power consumption multiplier for SM-MK1 module")
                .defineInRange("mk1_power_multiplier", 1.1, 0.1, 10.0);
        SPEED_MODULE_MK2_MULTIPLIER = COMMON_BUILDER.comment("Speed multiplier for SM-MK2 module")
                .defineInRange("mk2_speed_multiplier", 1.25, 0.1, 10.0);
        SPEED_MODULE_MK2_POWER_MULTIPLIER = COMMON_BUILDER.comment("Power consumption multiplier for SM-MK2 module")
                .defineInRange("mk2_power_multiplier", 1.25, 0.1, 10.0);
        SPEED_MODULE_MK3_MULTIPLIER = COMMON_BUILDER.comment("Speed multiplier for SM-MK3 module")
                .defineInRange("mk3_speed_multiplier", 1.5, 0.1, 10.0);
        SPEED_MODULE_MK3_POWER_MULTIPLIER = COMMON_BUILDER.comment("Power consumption multiplier for SM-MK3 module")
                .defineInRange("mk3_power_multiplier", 1.5, 0.1, 10.0);
        COMMON_BUILDER.pop();

        // Yield Modules
        COMMON_BUILDER.comment("Yield Module Configuration").push("yield_modules");
        YIELD_MODULE_MK1_MULTIPLIER = COMMON_BUILDER.comment("Yield multiplier for YM-MK1 module")
                .defineInRange("mk1_yield_multiplier", 1.1, 0.1, 10.0);
        YIELD_MODULE_MK1_SPEED_PENALTY = COMMON_BUILDER.comment("Speed penalty for YM-MK1 module (multiplier)")
                .defineInRange("mk1_speed_penalty", 0.95, 0.1, 1.0);
        YIELD_MODULE_MK2_MULTIPLIER = COMMON_BUILDER.comment("Yield multiplier for YM-MK2 module")
                .defineInRange("mk2_yield_multiplier", 1.25, 0.1, 10.0);
        YIELD_MODULE_MK2_SPEED_PENALTY = COMMON_BUILDER.comment("Speed penalty for YM-MK2 module (multiplier)")
                .defineInRange("mk2_speed_penalty", 0.85, 0.1, 1.0);
        YIELD_MODULE_MK3_MULTIPLIER = COMMON_BUILDER.comment("Yield multiplier for YM-MK3 module")
                .defineInRange("mk3_yield_multiplier", 1.5, 0.1, 10.0);
        YIELD_MODULE_MK3_SPEED_PENALTY = COMMON_BUILDER.comment("Speed penalty for YM-MK3 module (multiplier)")
                .defineInRange("mk3_speed_penalty", 0.75, 0.1, 1.0);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.pop();
    }

    private static void machineConfig() {
        COMMON_BUILDER.comment("Machine Settings").push(CATEGORY_MACHINES);

        planterConfig();
        composterConfig();
        burnerConfig();
        capacitorConfig();

        COMMON_BUILDER.pop();
    }

    private static void planterConfig() {
        COMMON_BUILDER.comment("Advanced Planter Configuration").push(CATEGORY_PLANTER);
        PLANTER_BASE_POWER_CONSUMPTION = COMMON_BUILDER.comment("Base power consumption for Advanced Planter (RF/t)")
                .defineInRange("base_power_consumption", 128, 1, 100000);
        PLANTER_BASE_PROCESSING_TIME = COMMON_BUILDER.comment("Base processing time for Planters (ticks)")
                .defineInRange("base_processing_time", 1200, 1, 72000);
        PLANTER_ENERGY_BUFFER = COMMON_BUILDER.comment("Energy buffer capacity for Advanced Planter (RF)")
                .defineInRange("energy_buffer", 100000, 1000, 10000000);
        COMMON_BUILDER.pop();
    }

    private static void composterConfig() {
        COMMON_BUILDER.comment("Composter Configuration").push(CATEGORY_COMPOSTER);
        COMPOSTER_BASE_POWER_CONSUMPTION = COMMON_BUILDER.comment("Base power consumption for Composter (RF/t)")
                .defineInRange("base_power_consumption", 128, 1, 100000);
        COMPOSTER_BASE_PROCESSING_TIME = COMMON_BUILDER.comment("Base processing time for Composter (ticks)")
                .defineInRange("base_processing_time", 600, 1, 72000);
        COMPOSTER_ENERGY_BUFFER = COMMON_BUILDER.comment("Energy buffer capacity for Composter (RF)")
                .defineInRange("energy_buffer", 100000, 1000, 10000000);
        COMPOSTER_ITEMS_PER_BIOMASS = COMMON_BUILDER.comment("Number of organic items required per biomass")
                .defineInRange("items_per_biomass", 32, 1, 256);
        COMMON_BUILDER.pop();
    }

    private static void burnerConfig() {
        COMMON_BUILDER.comment("Burner Configuration").push(CATEGORY_BURNER);
        BURNER_ENERGY_BUFFER = COMMON_BUILDER.comment("Energy buffer capacity for Burner (RF)")
                .defineInRange("energy_buffer", 100000, 1000, 10000000);
        BURNER_BIOMASS_RF_VALUE = COMMON_BUILDER.comment("RF generated per biomass item")
                .defineInRange("biomass_rf_value", 9000, 100, 100000);
        BURNER_COMPACTED_BIOMASS_RF_VALUE = COMMON_BUILDER.comment("RF generated per compacted biomass item")
                .defineInRange("compacted_biomass_rf_value", 18000, 1000, 1000000);
        BURNER_CRUDE_BIOMASS_RF_VALUE = COMMON_BUILDER.comment("RF generated per crude biomass item")
                .defineInRange("crude_biomass_rf_value", 1125, 50, 50000);
        COMMON_BUILDER.pop();
    }

    private static void capacitorConfig() {
        COMMON_BUILDER.comment("Capacitor Configuration").push(CATEGORY_CAPACITORS);

        COMMON_BUILDER.comment("Tier 1 Capacitor").push("tier_1");
        CAPACITOR_T1_BUFFER = COMMON_BUILDER.comment("Energy buffer capacity for T1 Capacitor (RF)")
                .defineInRange("buffer_capacity", 500000, 10000, 100000000);
        CAPACITOR_T1_TRANSFER_RATE = COMMON_BUILDER.comment("Energy transfer rate for T1 Capacitor (RF/t)")
                .defineInRange("transfer_rate", 512, 1, 100000);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Tier 2 Capacitor").push("tier_2");
        CAPACITOR_T2_BUFFER = COMMON_BUILDER.comment("Energy buffer capacity for T2 Capacitor (RF)")
                .defineInRange("buffer_capacity", 1000000, 10000, 100000000);
        CAPACITOR_T2_TRANSFER_RATE = COMMON_BUILDER.comment("Energy transfer rate for T2 Capacitor (RF/t)")
                .defineInRange("transfer_rate", 2048, 1, 100000);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Tier 3 Capacitor").push("tier_3");
        CAPACITOR_T3_BUFFER = COMMON_BUILDER.comment("Energy buffer capacity for T3 Capacitor (RF)")
                .defineInRange("buffer_capacity", 4000000, 10000, 100000000);
        CAPACITOR_T3_TRANSFER_RATE = COMMON_BUILDER.comment("Energy transfer rate for T3 Capacitor (RF/t)")
                .defineInRange("transfer_rate", 8192, 1, 100000);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.pop();
    }

    // ========================================
    // GETTER METHODS FOR MODULE EFFECTIVENESS
    // ========================================

    public static double getSpeedModuleMk1Multiplier() {
        return SPEED_MODULE_MK1_MULTIPLIER.get();
    }

    public static double getSpeedModuleMk1PowerMultiplier() {
        return SPEED_MODULE_MK1_POWER_MULTIPLIER.get();
    }

    public static double getSpeedModuleMk2Multiplier() {
        return SPEED_MODULE_MK2_MULTIPLIER.get();
    }

    public static double getSpeedModuleMk2PowerMultiplier() {
        return SPEED_MODULE_MK2_POWER_MULTIPLIER.get();
    }

    public static double getSpeedModuleMk3Multiplier() {
        return SPEED_MODULE_MK3_MULTIPLIER.get();
    }

    public static double getSpeedModuleMk3PowerMultiplier() {
        return SPEED_MODULE_MK3_POWER_MULTIPLIER.get();
    }

    public static double getYieldModuleMk1Multiplier() {
        return YIELD_MODULE_MK1_MULTIPLIER.get();
    }

    public static double getYieldModuleMk1SpeedPenalty() {
        return YIELD_MODULE_MK1_SPEED_PENALTY.get();
    }

    public static double getYieldModuleMk2Multiplier() {
        return YIELD_MODULE_MK2_MULTIPLIER.get();
    }

    public static double getYieldModuleMk2SpeedPenalty() {
        return YIELD_MODULE_MK2_SPEED_PENALTY.get();
    }

    public static double getYieldModuleMk3Multiplier() {
        return YIELD_MODULE_MK3_MULTIPLIER.get();
    }

    public static double getYieldModuleMk3SpeedPenalty() {
        return YIELD_MODULE_MK3_SPEED_PENALTY.get();
    }

    // ========================================
    // GETTER METHODS FOR MACHINE SETTINGS
    // ========================================

    // Advanced Planter Getters
    public static int getPlanterBasePowerConsumption() {
        return PLANTER_BASE_POWER_CONSUMPTION.get();
    }

    public static int getPlanterBaseProcessingTime() {
        return PLANTER_BASE_PROCESSING_TIME.get();
    }

    public static int getPlanterEnergyBuffer() {
        return PLANTER_ENERGY_BUFFER.get();
    }

    // Composter Getters
    public static int getComposterBasePowerConsumption() {
        return COMPOSTER_BASE_POWER_CONSUMPTION.get();
    }

    public static int getComposterBaseProcessingTime() {
        return COMPOSTER_BASE_PROCESSING_TIME.get();
    }

    public static int getComposterEnergyBuffer() {
        return COMPOSTER_ENERGY_BUFFER.get();
    }

    public static int getComposterItemsPerBiomass() {
        return COMPOSTER_ITEMS_PER_BIOMASS.get();
    }

    // Burner Getters
    public static int getBurnerEnergyBuffer() {
        return BURNER_ENERGY_BUFFER.get();
    }

    public static int getBurnerBiomassRfValue() {
        return BURNER_BIOMASS_RF_VALUE.get();
    }

    public static int getBurnerCompactedBiomassRfValue() {
        return BURNER_COMPACTED_BIOMASS_RF_VALUE.get();
    }

    public static int getBurnerCrudeBiomassRfValue() {
        return BURNER_CRUDE_BIOMASS_RF_VALUE.get();
    }

    // Capacitor Getters
    public static int getCapacitorT1Buffer() {
        return CAPACITOR_T1_BUFFER.get();
    }

    public static int getCapacitorT1TransferRate() {
        return CAPACITOR_T1_TRANSFER_RATE.get();
    }

    public static int getCapacitorT2Buffer() {
        return CAPACITOR_T2_BUFFER.get();
    }

    public static int getCapacitorT2TransferRate() {
        return CAPACITOR_T2_TRANSFER_RATE.get();
    }

    public static int getCapacitorT3Buffer() {
        return CAPACITOR_T3_BUFFER.get();
    }

    public static int getCapacitorT3TransferRate() {
        return CAPACITOR_T3_TRANSFER_RATE.get();
    }

    // ========================================
    // UTILITY METHODS
    // ========================================

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    public static void loadConfig() {
        // Load additional configs
        CompostableConfig.loadConfig();
        PlantablesConfig.loadConfig();
        LOGGER.info("AgriTech: Evolved configs reloaded");
    }

    // ========================================
    // EVENT HANDLING
    // ========================================

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // Cache compatibility values
        enableMysticalAgriculture = ENABLE_MYSTICAL_AGRICULTURE.get() && ModList.get().isLoaded("mysticalagriculture");
        enableMysticalAgradditions = ENABLE_MYSTICAL_AGRADDITIONS.get() && ModList.get().isLoaded("mysticalagradditions");
        enableFarmersDelight = ENABLE_FARMERS_DELIGHT.get() && ModList.get().isLoaded("farmersdelight");
        enableArsNouveau = ENABLE_ARS_NOUVEAU.get() && ModList.get().isLoaded("ars_nouveau");
        enableArsElemental = ENABLE_ARS_ELEMENTAL.get() && ModList.get().isLoaded("ars_elemental");
        enableSilentGear = ENABLE_SILENT_GEAR.get() && ModList.get().isLoaded("silentgear");
        enableJustDireThings = ENABLE_JUST_DIRE_THINGS.get() && ModList.get().isLoaded("justdirethings");
        enableImmersiveEngineering = ENABLE_IMMERSIVE_ENGINEERING.get() && ModList.get().isLoaded("immersiveengineering");
        enableEvilCraft = ENABLE_EVILCRAFT.get() && ModList.get().isLoaded("evilcraft");
        enableForbiddenArcanus = ENABLE_FORBIDDEN_ARCANUS.get() && ModList.get().isLoaded("forbidden_arcanus");
        enableIntegratedDynamics = ENABLE_INTEGRATED_DYNAMICS.get() && ModList.get().isLoaded("integrateddynamics");
        enableOccultism = ENABLE_OCCULTISM.get() && ModList.get().isLoaded("occultism");
        enableAgritechEvolved = ENABLE_ATE.get();

        LOGGER.info("AgriTech: Evolved configuration loaded");

        // Load additional configs
        CompostableConfig.loadConfig();
        PlantablesConfig.loadConfig();

        // Log mod compatibility info
        logModCompatibility();
    }

    private static void logModCompatibility() {
        LOGGER.info("Mod Compatibility Status:");
        if (enableMysticalAgriculture && ModList.get().isLoaded("mysticalagriculture")) {
            LOGGER.info("  - Mystical Agriculture: ENABLED");
        }
        if (enableMysticalAgradditions && ModList.get().isLoaded("mysticalagradditions")) {
            LOGGER.info("  - Mystical Agradditions: ENABLED");
        }
        if (enableFarmersDelight && ModList.get().isLoaded("farmersdelight")) {
            LOGGER.info("  - Farmer's Delight: ENABLED");
        }
        if (enableArsNouveau && ModList.get().isLoaded("ars_nouveau")) {
            LOGGER.info("  - Ars Nouveau: ENABLED");
        }
        if (enableArsElemental && ModList.get().isLoaded("ars_elemental")) {
            LOGGER.info("  - Ars Elemental: ENABLED");
        }
        if (enableSilentGear && ModList.get().isLoaded("silentgear")) {
            LOGGER.info("  - Silent Gear: ENABLED");
        }
        if (enableJustDireThings && ModList.get().isLoaded("justdirethings")) {
            LOGGER.info("  - Just Dire Things: ENABLED");
        }
        if (enableImmersiveEngineering && ModList.get().isLoaded("immersiveengineering")) {
            LOGGER.info("  - Immersive Engineering: ENABLED");
        }
        if (enableEvilCraft && ModList.get().isLoaded("evilcraft")) {
            LOGGER.info("  - EvilCraft: ENABLED");
        }
        if (enableForbiddenArcanus && ModList.get().isLoaded("forbidden_arcanus")) {
            LOGGER.info("  - Forbidden and Arcanus: ENABLED");
        }
        if (enableIntegratedDynamics && ModList.get().isLoaded("integrateddynamics")) {
            LOGGER.info("  - Integrated Dynamics: ENABLED");
        }
        if (enableOccultism && ModList.get().isLoaded("occultism")) {
            LOGGER.info("  - Occultism: ENABLED");
        }
    }
}