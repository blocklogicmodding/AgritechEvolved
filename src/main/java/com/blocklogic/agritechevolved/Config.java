package com.blocklogic.agritechevolved;

import com.blocklogic.agritechevolved.config.PlantablesConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ========================================
    // MOD COMPATIBILITY TOGGLES (Config 1)
    // ========================================

    private static final ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRICULTURE = BUILDER
            .comment("Enable Mystical Agriculture compatibility")
            .define("enableMysticalAgriculture", true);

    private static final ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRADDITIONS = BUILDER
            .comment("Enable Mystical Agradditions compatibility")
            .define("enableMysticalAgradditions", true);

    private static final ModConfigSpec.BooleanValue ENABLE_FARMERS_DELIGHT = BUILDER
            .comment("Enable Farmer's Delight compatibility")
            .define("enableFarmersDelight", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ARS_NOUVEAU = BUILDER
            .comment("Enable Ars Nouveau compatibility")
            .define("enableArsNouveau", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ARS_ELEMENTAL = BUILDER
            .comment("Enable Ars Elemental compatibility")
            .define("enableArsElemental", true);

    private static final ModConfigSpec.BooleanValue ENABLE_SILENT_GEAR = BUILDER
            .comment("Enable Silent Gear compatibility")
            .define("enableSilentGear", true);

    private static final ModConfigSpec.BooleanValue ENABLE_JUST_DIRE_THINGS = BUILDER
            .comment("Enable Just Dire Things compatibility")
            .define("enableJustDireThings", true);

    private static final ModConfigSpec.BooleanValue ENABLE_IMMERSIVE_ENGINEERING = BUILDER
            .comment("Enable Immersive Engineering compatibility")
            .define("enableImmersiveEngineering", true);

    private static final ModConfigSpec.BooleanValue ENABLE_EVILCRAFT = BUILDER
            .comment("Enable EvilCraft compatibility")
            .define("enableEvilCraft", true);

    private static final ModConfigSpec.BooleanValue ENABLE_FORBIDDEN_ARCANUS = BUILDER
            .comment("Enable Forbidden and Arcanus compatibility")
            .define("enableForbiddenArcanus", true);

    private static final ModConfigSpec.BooleanValue ENABLE_INTEGRATED_DYNAMICS = BUILDER
            .comment("Enable Integrated Dynamics compatibility")
            .define("enableIntegratedDynamics", true);

    private static final ModConfigSpec.BooleanValue ENABLE_OCCULTISM = BUILDER
            .comment("Enable Occultism compatibility")
            .define("enableOccultism", true);

    private static final ModConfigSpec.BooleanValue ENABLE_HEXEREI = BUILDER
            .comment("Enable Hexerei compatibility")
            .define("enableHexerei", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ATE = BUILDER
            .comment("Enable AgriTech: Evolved internal content")
            .define("enableAgritechEvolved", true);

    // ========================================
    // MODULE EFFECTIVENESS CONFIGURATION (Config 3)
    // ========================================

    // Speed Module Configuration
    private static final ModConfigSpec.DoubleValue SPEED_MODULE_MK1_MULTIPLIER = BUILDER
            .comment("Speed multiplier for SM-MK1 module")
            .defineInRange("speedModuleMk1Multiplier", 1.1, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue SPEED_MODULE_MK1_POWER_MULTIPLIER = BUILDER
            .comment("Power consumption multiplier for SM-MK1 module")
            .defineInRange("speedModuleMk1PowerMultiplier", 1.1, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue SPEED_MODULE_MK2_MULTIPLIER = BUILDER
            .comment("Speed multiplier for SM-MK2 module")
            .defineInRange("speedModuleMk2Multiplier", 1.25, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue SPEED_MODULE_MK2_POWER_MULTIPLIER = BUILDER
            .comment("Power consumption multiplier for SM-MK2 module")
            .defineInRange("speedModuleMk2PowerMultiplier", 1.25, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue SPEED_MODULE_MK3_MULTIPLIER = BUILDER
            .comment("Speed multiplier for SM-MK3 module")
            .defineInRange("speedModuleMk3Multiplier", 1.5, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue SPEED_MODULE_MK3_POWER_MULTIPLIER = BUILDER
            .comment("Power consumption multiplier for SM-MK3 module")
            .defineInRange("speedModuleMk3PowerMultiplier", 1.5, 0.1, 10.0);

    // Yield Module Configuration
    private static final ModConfigSpec.DoubleValue YIELD_MODULE_MK1_MULTIPLIER = BUILDER
            .comment("Yield multiplier for YM-MK1 module")
            .defineInRange("yieldModuleMk1Multiplier", 1.1, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue YIELD_MODULE_MK1_SPEED_PENALTY = BUILDER
            .comment("Speed penalty for YM-MK1 module (multiplier)")
            .defineInRange("yieldModuleMk1SpeedPenalty", 0.95, 0.1, 1.0);

    private static final ModConfigSpec.DoubleValue YIELD_MODULE_MK2_MULTIPLIER = BUILDER
            .comment("Yield multiplier for YM-MK2 module")
            .defineInRange("yieldModuleMk2Multiplier", 1.25, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue YIELD_MODULE_MK2_SPEED_PENALTY = BUILDER
            .comment("Speed penalty for YM-MK2 module (multiplier)")
            .defineInRange("yieldModuleMk2SpeedPenalty", 0.85, 0.1, 1.0);

    private static final ModConfigSpec.DoubleValue YIELD_MODULE_MK3_MULTIPLIER = BUILDER
            .comment("Yield multiplier for YM-MK3 module")
            .defineInRange("yieldModuleMk3Multiplier", 1.5, 0.1, 10.0);

    private static final ModConfigSpec.DoubleValue YIELD_MODULE_MK3_SPEED_PENALTY = BUILDER
            .comment("Speed penalty for YM-MK3 module (multiplier)")
            .defineInRange("yieldModuleMk3SpeedPenalty", 0.75, 0.1, 1.0);


    // ========================================
    // MACHINE SETTINGS CONFIGURATION (Config 3)
    // ========================================


    // Advanced Planter Configuration
    private static final ModConfigSpec.IntValue PLANTER_BASE_POWER_CONSUMPTION = BUILDER
            .comment("Base power consumption for Advanced Planter (RF/t)")
            .defineInRange("planterBasePowerConsumption", 128, 1, 100000);

    private static final ModConfigSpec.IntValue PLANTER_BASE_PROCESSING_TIME = BUILDER
            .comment("Base processing time for Advanced Planter (ticks)")
            .defineInRange("planterBaseProcessingTime", 1200, 1, 72000);

    private static final ModConfigSpec.IntValue PLANTER_ENERGY_BUFFER = BUILDER
            .comment("Energy buffer capacity for Advanced Planter (RF)")
            .defineInRange("planterEnergyBuffer", 20000, 1000, 10000000);

    // Composter Configuration
    private static final ModConfigSpec.IntValue COMPOSTER_BASE_POWER_CONSUMPTION = BUILDER
            .comment("Base power consumption for Composter (RF/t)")
            .defineInRange("composterBasePowerConsumption", 128, 1, 100000);

    private static final ModConfigSpec.IntValue COMPOSTER_BASE_PROCESSING_TIME = BUILDER
            .comment("Base processing time for Composter (ticks)")
            .defineInRange("composterBaseProcessingTime", 600, 1, 72000);

    private static final ModConfigSpec.IntValue COMPOSTER_ENERGY_BUFFER = BUILDER
            .comment("Energy buffer capacity for Composter (RF)")
            .defineInRange("composterEnergyBuffer", 20000, 1000, 10000000);

    private static final ModConfigSpec.IntValue COMPOSTER_ITEMS_PER_BIOMASS = BUILDER
            .comment("Number of organic items required per biomass")
            .defineInRange("composterItemsPerBiomass", 32, 1, 256);

    // Infuser Configuration
    private static final ModConfigSpec.IntValue INFUSER_BASE_POWER_CONSUMPTION = BUILDER
            .comment("Base power consumption for Infuser (RF/t)")
            .defineInRange("infuserBasePowerConsumption", 512, 1, 100000);

    private static final ModConfigSpec.IntValue INFUSER_BASE_PROCESSING_TIME = BUILDER
            .comment("Base processing time for Infuser (ticks)")
            .defineInRange("infuserBaseProcessingTime", 600, 1, 72000);

    private static final ModConfigSpec.IntValue INFUSER_ENERGY_BUFFER = BUILDER
            .comment("Energy buffer capacity for Infuser (RF)")
            .defineInRange("infuserEnergyBuffer", 20000, 1000, 10000000);

    private static final ModConfigSpec.IntValue INFUSER_ITEMS_PER_FARMLAND = BUILDER
            .comment("Number of precious materials required per infused farmland")
            .defineInRange("infuserItemsPerFarmland", 32, 1, 256);

    // Burner Configuration
    private static final ModConfigSpec.IntValue BURNER_ENERGY_BUFFER = BUILDER
            .comment("Energy buffer capacity for Burner (RF)")
            .defineInRange("burnerEnergyBuffer", 20000, 1000, 10000000);

    private static final ModConfigSpec.IntValue BURNER_BIOMASS_RF_VALUE = BUILDER
            .comment("RF generated per biomass item")
            .defineInRange("burnerBiomassRfValue", 4000, 100, 100000);

    private static final ModConfigSpec.IntValue BURNER_COMPACTED_BIOMASS_RF_VALUE = BUILDER
            .comment("RF generated per compacted biomass item")
            .defineInRange("burnerCompactedBiomassRfValue", 36000, 1000, 1000000);

    // Capacitor Configuration
    private static final ModConfigSpec.IntValue CAPACITOR_T1_BUFFER = BUILDER
            .comment("Energy buffer capacity for T1 Capacitor (RF)")
            .defineInRange("capacitorT1Buffer", 500000, 10000, 100000000);

    private static final ModConfigSpec.IntValue CAPACITOR_T1_TRANSFER_RATE = BUILDER
            .comment("Energy transfer rate for T1 Capacitor (RF/t)")
            .defineInRange("capacitorT1TransferRate", 512, 1, 100000);

    private static final ModConfigSpec.IntValue CAPACITOR_T2_BUFFER = BUILDER
            .comment("Energy buffer capacity for T2 Capacitor (RF)")
            .defineInRange("capacitorT2Buffer", 1000000, 10000, 100000000);

    private static final ModConfigSpec.IntValue CAPACITOR_T2_TRANSFER_RATE = BUILDER
            .comment("Energy transfer rate for T2 Capacitor (RF/t)")
            .defineInRange("capacitorT2TransferRate", 2048, 1, 100000);

    private static final ModConfigSpec.IntValue CAPACITOR_T3_BUFFER = BUILDER
            .comment("Energy buffer capacity for T3 Capacitor (RF)")
            .defineInRange("capacitorT3Buffer", 4000000, 10000, 100000000);

    private static final ModConfigSpec.IntValue CAPACITOR_T3_TRANSFER_RATE = BUILDER
            .comment("Energy transfer rate for T3 Capacitor (RF/t)")
            .defineInRange("capacitorT3TransferRate", 8192, 1, 100000);


    static final ModConfigSpec SPEC = BUILDER.build();

    // ========================================
    // STATIC CONFIGURATION VALUES
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
    public static boolean enableHexerei;
    public static boolean enableAgritechEvolved;

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

    // Advanced Planter
    public static int getPlanterBasePowerConsumption() {
        return PLANTER_BASE_POWER_CONSUMPTION.get();
    }

    public static int getPlanterBaseProcessingTime() {
        return PLANTER_BASE_PROCESSING_TIME.get();
    }

    public static int getPlanterEnergyBuffer() {
        return PLANTER_ENERGY_BUFFER.get();
    }

    // Composter
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

    // Infuser
    public static int getInfuserBasePowerConsumption() {
        return INFUSER_BASE_POWER_CONSUMPTION.get();
    }

    public static int getInfuserBaseProcessingTime() {
        return INFUSER_BASE_PROCESSING_TIME.get();
    }

    public static int getInfuserEnergyBuffer() {
        return INFUSER_ENERGY_BUFFER.get();
    }

    public static int getInfuserItemsPerFarmland() {
        return INFUSER_ITEMS_PER_FARMLAND.get();
    }

    // Burner
    public static int getBurnerEnergyBuffer() {
        return BURNER_ENERGY_BUFFER.get();
    }

    public static int getBurnerBiomassRfValue() {
        return BURNER_BIOMASS_RF_VALUE.get();
    }

    public static int getBurnerCompactedBiomassRfValue() {
        return BURNER_COMPACTED_BIOMASS_RF_VALUE.get();
    }

    // Capacitors
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

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    public static void loadConfig() {
        PlantablesConfig.loadConfig();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
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
        enableHexerei = ENABLE_HEXEREI.get() && ModList.get().isLoaded("hexerei");
        enableAgritechEvolved = ENABLE_ATE.get();

        PlantablesConfig.loadConfig();
    }
}