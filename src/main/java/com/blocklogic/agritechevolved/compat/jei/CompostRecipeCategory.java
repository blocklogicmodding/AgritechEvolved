package com.blocklogic.agritechevolved.compat.jei;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.AgritechEvolved;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CompostRecipeCategory implements IRecipeCategory<CompostRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "composting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "textures/gui/jei/jei_composter_gui.png");
    public static final RecipeType<CompostRecipe> COMPOST_RECIPE_TYPE = new RecipeType<>(UID, CompostRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CompostRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 134, 72);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ATEBlocks.COMPOSTER.get()));
    }

    @Override
    public RecipeType<CompostRecipe> getRecipeType() {
        return COMPOST_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.agritechevolved.composting.tooltip");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 134;
    }

    @Override
    public int getHeight() {
        return 72;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompostRecipe recipe, IFocusGroup focuses) {
        ItemStack[] inputItems = recipe.getInput().getItems();
        if (inputItems.length > 0) {
            ItemStack displayStack = inputItems[0].copy();
            displayStack.setCount(32);
            builder.addSlot(RecipeIngredientRole.INPUT, 10, 10)
                    .addItemStack(displayStack);
        }

        int outputIndex = 0;
        for (ItemStack output : recipe.getOutputs()) {
            int x = 106;
            int y = 10 + outputIndex * 18;
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(output);
            outputIndex++;
        }
    }

    @Override
    public void draw(CompostRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}