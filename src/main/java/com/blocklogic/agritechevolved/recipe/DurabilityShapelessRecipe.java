package com.blocklogic.agritechevolved.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class DurabilityShapelessRecipe implements CraftingRecipe {
    private final String group;
    private final CraftingBookCategory category;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final Ingredient toolIngredient;
    private final int durabilityPerItem;

    public DurabilityShapelessRecipe(String group, CraftingBookCategory category, ItemStack result,
                                     NonNullList<Ingredient> ingredients, Ingredient toolIngredient,
                                     int durabilityPerItem) {
        this.group = group;
        this.category = category;
        this.result = result;
        this.ingredients = ingredients;
        this.toolIngredient = toolIngredient;
        this.durabilityPerItem = durabilityPerItem;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        NonNullList<Ingredient> requiredIngredients = NonNullList.create();
        requiredIngredients.addAll(ingredients);

        boolean foundTool = false;
        int totalProcessableItems = 0;
        ItemStack foundToolStack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack inputStack = input.getItem(i);
            if (inputStack.isEmpty()) continue;

            if (toolIngredient.test(inputStack)) {
                if (foundTool) return false;
                foundTool = true;
                foundToolStack = inputStack;
                continue;
            }

            boolean matchedIngredient = false;
            for (int j = 0; j < requiredIngredients.size(); j++) {
                if (requiredIngredients.get(j).test(inputStack)) {
                    totalProcessableItems += 1;
                    requiredIngredients.remove(j);
                    matchedIngredient = true;
                    break;
                }
            }

            if (!matchedIngredient) {
                return false;
            }
        }

        if (!foundTool || !requiredIngredients.isEmpty()) {
            return false;
        }

        if (foundToolStack.isDamageableItem()) {
            int currentDamage = foundToolStack.getDamageValue();
            int maxDurability = foundToolStack.getMaxDamage();
            int neededDurability = totalProcessableItems * durabilityPerItem;

            return (currentDamage + neededDurability) < maxDurability;
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int processableItems = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && !toolIngredient.test(stack)) {
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.test(stack)) {
                        processableItems += 1;
                        break;
                    }
                }
            }
        }

        ItemStack result = this.result.copy();
        result.setCount(processableItems);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size() + 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        ItemStack toolStack = ItemStack.EMPTY;
        int toolSlot = -1;
        int processableItems = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (toolIngredient.test(stack)) {
                    toolStack = stack.copy();
                    toolSlot = i;
                } else {
                    for (Ingredient ingredient : ingredients) {
                        if (ingredient.test(stack)) {
                            processableItems += 1;
                            break;
                        }
                    }
                }
            }
        }

        if (!toolStack.isEmpty() && toolSlot != -1) {
            int totalDurabilityNeeded = processableItems * durabilityPerItem;

            if (toolStack.isDamageableItem()) {
                int currentDamage = toolStack.getDamageValue();
                int maxDurability = toolStack.getMaxDamage();
                int newDamage = currentDamage + totalDurabilityNeeded;

                if (newDamage >= maxDurability) {
                    remaining.set(toolSlot, ItemStack.EMPTY);
                } else {
                    toolStack.setDamageValue(newDamage);
                    remaining.set(toolSlot, toolStack);
                }
            } else {
                remaining.set(toolSlot, toolStack);
            }
        }

        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ATERecipes.DURABILITY_SHAPELESS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public Ingredient getToolIngredient() {
        return toolIngredient;
    }

    public int getDurabilityPerItem() {
        return durabilityPerItem;
    }
}