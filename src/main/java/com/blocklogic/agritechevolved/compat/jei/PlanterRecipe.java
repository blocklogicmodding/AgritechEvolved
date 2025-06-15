package com.blocklogic.agritechevolved.compat.jei;

import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class PlanterRecipe implements IRecipeCategoryExtension {
    private final Ingredient plantableIngredient;
    private final Ingredient soilIngredient;
    private final List<ItemStack> outputs;
    private final PlantableType type;

    public enum PlantableType {
        CROP, TREE
    }

    public PlanterRecipe(Ingredient plantableIngredient, Ingredient soilIngredient, List<ItemStack> outputs, PlantableType type) {
        this.plantableIngredient = plantableIngredient;
        this.soilIngredient = soilIngredient;
        this.outputs = outputs;
        this.type = type;
    }

    public Ingredient getPlantableIngredient() {
        return plantableIngredient;
    }

    public Ingredient getSeedIngredient() {
        return plantableIngredient;
    }

    public Ingredient getSaplingIngredient() {
        return plantableIngredient;
    }

    public Ingredient getSoilIngredient() {
        return soilIngredient;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public PlantableType getType() {
        return type;
    }

    public static PlanterRecipe createCrop(String seedId, String soilId) {
        return create(seedId, soilId, PlantableType.CROP);
    }

    public static PlanterRecipe createTree(String saplingId, String soilId) {
        return create(saplingId, soilId, PlantableType.TREE);
    }

    private static PlanterRecipe create(String plantableId, String soilId, PlantableType type) {
        Item plantableItem = RegistryHelper.getItem(plantableId);
        if (plantableItem == null) {
            LogUtils.getLogger().error("Failed to create planter recipe: {} item not found for ID: {}",
                    type == PlantableType.CROP ? "Seed" : "Sapling", plantableId);
            throw new IllegalArgumentException((type == PlantableType.CROP ? "Seed" : "Sapling") + " item not found for ID: " + plantableId);
        }

        Block soilBlock = RegistryHelper.getBlock(soilId);
        if (soilBlock == null) {
            LogUtils.getLogger().error("Failed to create planter recipe: Soil block not found for ID: {}", soilId);
            throw new IllegalArgumentException("Soil block not found for ID: " + soilId);
        }

        Ingredient plantableIngredient = Ingredient.of(plantableItem);
        Ingredient soilIngredient = Ingredient.of(soilBlock.asItem());

        List<ItemStack> outputs = new ArrayList<>();
        List<? extends PlantablesConfig.DropInfo> drops = type == PlantableType.CROP
                ? PlantablesConfig.getCropDrops(plantableId)
                : PlantablesConfig.getTreeDrops(plantableId);

        for (PlantablesConfig.DropInfo dropInfo : drops) {
            if (dropInfo.chance > 0) {
                Item dropItem = RegistryHelper.getItem(dropInfo.item);
                if (dropItem != null) {
                    ItemStack outputStack = new ItemStack(
                            dropItem,
                            (dropInfo.minCount + dropInfo.maxCount) / 2
                    );
                    outputs.add(outputStack);
                } else {
                    LogUtils.getLogger().error("Drop item not found for ID: {} in recipe for {} {}",
                            dropInfo.item, type == PlantableType.CROP ? "seed" : "sapling", plantableId);
                    throw new IllegalArgumentException("Drop item not found for ID: " + dropInfo.item + " in recipe for " +
                            (type == PlantableType.CROP ? "seed" : "sapling") + " " + plantableId);
                }
            }
        }

        return new PlanterRecipe(plantableIngredient, soilIngredient, outputs, type);
    }
}