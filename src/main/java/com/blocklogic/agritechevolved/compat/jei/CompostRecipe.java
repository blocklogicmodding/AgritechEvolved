package com.blocklogic.agritechevolved.compat.jei;

import com.blocklogic.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class CompostRecipe implements IRecipeCategoryExtension {
    private final Ingredient input;
    private final List<ItemStack> outputs;

    public CompostRecipe(Ingredient input, List<ItemStack> outputs) {
        this.input = input;
        this.outputs = outputs;
    }

    public Ingredient getInput() {
        return input;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public static CompostRecipe create(String itemId) {
        RegistryHelper registry = new RegistryHelper();
        Item item = registry.getItem(itemId);

        if (item == null) {
            LogUtils.getLogger().error("Failed to create compost recipe: Item not found for ID: {}", itemId);
            throw new IllegalArgumentException("Item not found for ID: " + itemId);
        }

        Ingredient input = Ingredient.of(item);

        Item biomassItem = registry.getItem("agritechevolved:biomass");
        if (biomassItem == null) {
            LogUtils.getLogger().error("Failed to create compost recipe: Biomass item not found");
            throw new IllegalArgumentException("Biomass item not found");
        }

        List<ItemStack> outputs = List.of(new ItemStack(biomassItem, 1));

        return new CompostRecipe(input, outputs);
    }
}