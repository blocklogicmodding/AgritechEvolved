package com.blocklogic.agritechevolved;

import java.util.List;

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

    private static final ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRICULTURE = BUILDER
            .define("enableMysticalAgriculture", true);

    private static final ModConfigSpec.BooleanValue ENABLE_MYSTICAL_AGRADDITIONS = BUILDER
            .define("enableMysticalAgradditions", true);

    private static final ModConfigSpec.BooleanValue ENABLE_FARMERS_DELIGHT = BUILDER
            .define("enableFarmersDelight", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ARS_NOUVEAU = BUILDER
            .define("enableArsNouveau", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ARS_ELEMENTAL = BUILDER
            .define("enableArsElemental", true);

    private static final ModConfigSpec.BooleanValue ENABLE_SILENT_GEAR = BUILDER
            .define("enableSilentGear", true);

    private static final ModConfigSpec.BooleanValue ENABLE_JUST_DIRE_THINGS_SOIL = BUILDER
            .define("enableJustDireThingsSoils", true);

    private static final ModConfigSpec.BooleanValue ENABLE_IMMERSIVE_ENGINEERING = BUILDER
            .define("enableImmersiveEngineering", true);

    private static final ModConfigSpec.BooleanValue ENABLE_EVILCRAFT = BUILDER
            .define("enableEvilCraft", true);

    private static final ModConfigSpec.BooleanValue ENABLE_FORBIDDEN_ARCANUS = BUILDER
            .define("enableForbiddenArcanus", true);

    private static final ModConfigSpec.BooleanValue ENABLE_INTEGRATED_DYNAMICS = BUILDER
            .define("enableIntegratedDynamics", true);

    private static final ModConfigSpec.BooleanValue ENABLE_OCCULTISM = BUILDER
            .define("enableOccultism", true);

    private static final ModConfigSpec.BooleanValue ENABLE_HEXEREI = BUILDER
            .define("enableHexerei", true);

    private static final ModConfigSpec.BooleanValue ENABLE_ATE_SOILS = BUILDER
            .define("enableAgeitechEvolvedSoils", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableMysticalAgriculture;
    public static boolean enableMysticalAgradditions;
    public static boolean enableFarmersDelight;
    public static boolean enableArsNouveau;
    public static boolean enableArsElemental;
    public static boolean enableSilentGear;
    public static boolean enableJustDireThingSoils;
    public static boolean enableImmersiveEngineering;
    public static boolean enableEvilCraft;
    public static boolean enableForbiddenArcanus;
    public static boolean enableIntegratedDynamics;
    public static boolean enableOccultism;
    public static boolean enableHexerei;
    public static boolean enableAgeitechEvolvedSoils;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    public static void loadConfig() {
        enableMysticalAgriculture = ENABLE_MYSTICAL_AGRICULTURE.get() && ModList.get().isLoaded("mysticalagriculture");
        enableMysticalAgradditions = ENABLE_MYSTICAL_AGRADDITIONS.get() && ModList.get().isLoaded("mysticalagradditions");
        enableFarmersDelight = ENABLE_FARMERS_DELIGHT.get() && ModList.get().isLoaded("farmersdelight");
        enableArsNouveau = ENABLE_ARS_NOUVEAU.get() && ModList.get().isLoaded("ars_nouveau");
        enableArsElemental = ENABLE_ARS_ELEMENTAL.get() && ModList.get().isLoaded("ars_elemental");
        enableSilentGear = ENABLE_SILENT_GEAR.get() && ModList.get().isLoaded("silentgear");
        enableJustDireThingSoils = ENABLE_JUST_DIRE_THINGS_SOIL.get() && ModList.get().isLoaded("justdirethings");
        enableImmersiveEngineering = ENABLE_IMMERSIVE_ENGINEERING.get() && ModList.get().isLoaded("immersiveengineering");
        enableEvilCraft = ENABLE_EVILCRAFT.get() && ModList.get().isLoaded("evilcraft");
        enableForbiddenArcanus = ENABLE_FORBIDDEN_ARCANUS.get() && ModList.get().isLoaded("forbidden_arcanus");
        enableIntegratedDynamics = ENABLE_INTEGRATED_DYNAMICS.get() && ModList.get().isLoaded("integrateddynamics");
        enableOccultism = ENABLE_OCCULTISM.get() && ModList.get().isLoaded("occultism");
        enableHexerei = ENABLE_HEXEREI.get() && ModList.get().isLoaded("hexerei");
        enableAgeitechEvolvedSoils = ENABLE_ATE_SOILS.get() && ModList.get().isLoaded("agritechevolved");
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        loadConfig();
        PlantablesConfig.loadConfig();
    }
}
