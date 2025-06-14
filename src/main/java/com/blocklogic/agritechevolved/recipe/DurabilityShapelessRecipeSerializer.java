package com.blocklogic.agritechevolved.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DurabilityShapelessRecipeSerializer implements RecipeSerializer<DurabilityShapelessRecipe> {

    public static final MapCodec<DurabilityShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(DurabilityShapelessRecipe::getGroup),
                    CraftingBookCategory.CODEC.fieldOf("category").forGetter(DurabilityShapelessRecipe::category),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null)),
                    Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(DurabilityShapelessRecipe::getIngredients),
                    Ingredient.CODEC_NONEMPTY.fieldOf("tool").forGetter(DurabilityShapelessRecipe::getToolIngredient),
                    Codec.INT.fieldOf("durability_per_item").forGetter(DurabilityShapelessRecipe::getDurabilityPerItem)
            ).apply(instance, (group, category, result, ingredients, tool, durability) -> {
                NonNullList<Ingredient> nonNullIngredients = NonNullList.create();
                nonNullIngredients.addAll(ingredients);
                return new DurabilityShapelessRecipe(group, category, result, nonNullIngredients, tool, durability);
            })
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DurabilityShapelessRecipe> STREAM_CODEC =
            StreamCodec.of(
                    DurabilityShapelessRecipeSerializer::toNetwork,
                    DurabilityShapelessRecipeSerializer::fromNetwork
            );

    @Override
    public MapCodec<DurabilityShapelessRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DurabilityShapelessRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, DurabilityShapelessRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeEnum(recipe.category());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getResultItem(null));

        buffer.writeVarInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
        }

        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getToolIngredient());
        buffer.writeVarInt(recipe.getDurabilityPerItem());
    }

    private static DurabilityShapelessRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);

        int ingredientCount = buffer.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
        for (int i = 0; i < ingredientCount; i++) {
            ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
        }

        Ingredient tool = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        int durabilityPerItem = buffer.readVarInt();

        return new DurabilityShapelessRecipe(group, category, result, ingredients, tool, durabilityPerItem);
    }
}