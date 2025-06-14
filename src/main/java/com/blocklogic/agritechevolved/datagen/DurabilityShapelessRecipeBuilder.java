package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.recipe.DurabilityShapelessRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DurabilityShapelessRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private Ingredient toolIngredient = Ingredient.EMPTY;
    private int durabilityPerItem = 1;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private CraftingBookCategory category = CraftingBookCategory.MISC;

    public DurabilityShapelessRecipeBuilder(ItemLike result, int count) {
        this.result = new ItemStack(result, count);
    }

    public static DurabilityShapelessRecipeBuilder shapeless(ItemLike result) {
        return new DurabilityShapelessRecipeBuilder(result, 1);
    }

    public static DurabilityShapelessRecipeBuilder shapeless(ItemLike result, int count) {
        return new DurabilityShapelessRecipeBuilder(result, count);
    }

    public DurabilityShapelessRecipeBuilder requires(ItemLike item) {
        return requires(item, 1);
    }

    public DurabilityShapelessRecipeBuilder requires(ItemLike item, int count) {
        for (int i = 0; i < count; i++) {
            ingredients.add(Ingredient.of(item));
        }
        return this;
    }

    public DurabilityShapelessRecipeBuilder requires(Ingredient ingredient) {
        ingredients.add(ingredient);
        return this;
    }

    public DurabilityShapelessRecipeBuilder requires(TagKey<Item> tag) {
        ingredients.add(Ingredient.of(tag));
        return this;
    }

    public DurabilityShapelessRecipeBuilder tool(ItemLike tool) {
        this.toolIngredient = Ingredient.of(tool);
        return this;
    }

    public DurabilityShapelessRecipeBuilder tool(Ingredient tool) {
        this.toolIngredient = tool;
        return this;
    }

    public DurabilityShapelessRecipeBuilder durabilityPerItem(int durability) {
        this.durabilityPerItem = durability;
        return this;
    }

    public DurabilityShapelessRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public DurabilityShapelessRecipeBuilder category(CraftingBookCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        ensureValid(id);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        criteria.forEach(advancementBuilder::addCriterion);

        DurabilityShapelessRecipe recipe = new DurabilityShapelessRecipe(
                group == null ? "" : group,
                category,
                result,
                ingredients,
                toolIngredient,
                durabilityPerItem
        );

        String categoryPath = switch (category) {
            case BUILDING -> "building_blocks";
            case REDSTONE -> "redstone";
            case EQUIPMENT -> "equipment";
            case MISC -> "misc";
        };

        output.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/" + categoryPath + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
        if (toolIngredient.isEmpty()) {
            throw new IllegalStateException("No tool specified for recipe " + id);
        }
    }
}