package com.blocklogic.agritechevolved.compat.jei;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class ATEJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new PlanterRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<PlanterRecipe> planterRecipes = generatePlanterRecipes();
        registration.addRecipes(PlanterRecipeCategory.PLANTER_RECIPE_TYPE, planterRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(ATEBlocks.ADVANCED_PLANTER.get()),
                PlanterRecipeCategory.PLANTER_RECIPE_TYPE
        );
    }

    private List<PlanterRecipe> generatePlanterRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();

        recipes.addAll(generateCropRecipes());
        recipes.addAll(generateTreeRecipes());

        LogUtils.getLogger().info("Generated {} total planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<PlanterRecipe> generateCropRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        Map<String, List<String>> seedToSoilMap = PlantablesConfig.getAllSeedToSoilMappings();

        for (Map.Entry<String, List<String>> entry : seedToSoilMap.entrySet()) {
            String seedId = entry.getKey();

            for (String soilId : entry.getValue()) {
                try {
                    Block soilBlock = RegistryHelper.getBlock(soilId);
                    if (soilBlock == null) {
                        LogUtils.getLogger().error("Invalid soil block in config: {} for seed {}", soilId, seedId);
                        continue;
                    }

                    PlanterRecipe recipe = PlanterRecipe.createCrop(seedId, soilId);
                    if (recipe != null && !recipe.getOutputs().isEmpty()) {
                        recipes.add(recipe);
                    }
                } catch (Exception e) {
                    LogUtils.getLogger().error("Error creating recipe for seed {} and soil {}: {}",
                            seedId, soilId, e.getMessage(), e);
                }
            }
        }

        LogUtils.getLogger().info("Generated {} crop planter recipes for JEI", recipes.size());
        return recipes;
    }

    private List<PlanterRecipe> generateTreeRecipes() {
        List<PlanterRecipe> recipes = new ArrayList<>();
        Map<String, List<String>> saplingToSoilMap = PlantablesConfig.getAllSaplingToSoilMappings();

        for (Map.Entry<String, List<String>> entry : saplingToSoilMap.entrySet()) {
            String saplingId = entry.getKey();

            for (String soilId : entry.getValue()) {
                try {
                    Block soilBlock = RegistryHelper.getBlock(soilId);
                    if (soilBlock == null) {
                        LogUtils.getLogger().error("Invalid soil block in config: {} for sapling {}", soilId, saplingId);
                        continue;
                    }

                    PlanterRecipe recipe = PlanterRecipe.createTree(saplingId, soilId);
                    if (recipe != null && !recipe.getOutputs().isEmpty()) {
                        recipes.add(recipe);
                    }
                } catch (Exception e) {
                    LogUtils.getLogger().error("Error creating recipe for sapling {} and soil {}: {}",
                            saplingId, soilId, e.getMessage(), e);
                }
            }
        }

        LogUtils.getLogger().info("Generated {} tree planter recipes for JEI", recipes.size());
        return recipes;
    }

    private static IJeiRuntime jeiRuntime;

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        ATEJeiPlugin.jeiRuntime = jeiRuntime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }
}