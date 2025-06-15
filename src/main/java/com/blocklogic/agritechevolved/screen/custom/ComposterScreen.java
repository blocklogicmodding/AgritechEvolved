package com.blocklogic.agritechevolved.screen.custom;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.compat.jei.ATEJeiPlugin;
import com.blocklogic.agritechevolved.compat.jei.CompostRecipeCategory;
import com.blocklogic.agritechevolved.compat.jei.PlanterRecipeCategory;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComposterScreen extends AbstractContainerScreen<ComposterMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "textures/gui/composter_gui.png");

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public ComposterScreen(ComposterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 166;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (maxProgress > 0) {
            float progressPercentage = (float) progress / maxProgress;
            int progressBarHeight = (int) (52 * progressPercentage);
            int progressBarY = y + 15 + 52 - progressBarHeight;

            guiGraphics.blit(GUI_TEXTURE,
                    x + 85, progressBarY,
                    186, 52 - progressBarHeight,
                    6, progressBarHeight
            );
        }

        int energyStored = this.menu.getEnergyStored();
        int maxEnergy = this.menu.getMaxEnergyStored();
        if (maxEnergy > 0) {
            float energyPercentage = (float) energyStored / maxEnergy;
            int energyBarHeight = (int) (52 * energyPercentage);
            int energyBarY = y + 15 + 52 - energyBarHeight;

            guiGraphics.blit(GUI_TEXTURE,
                    x + 158, energyBarY,
                    176, 52 - energyBarHeight,
                    10, energyBarHeight
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        if (x >= guiX + 85 && x <= guiX + 85 + 6 && y >= guiY + 15 && y <= guiY + 15 + 52) {
            List<Component> tooltip = new ArrayList<>();
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            int organicItems = this.menu.getOrganicItemsCollected();
            int requiredItems = this.menu.getRequiredOrganicItems();

            tooltip.add(Component.translatable("tooltip.agritechevolved.composting_progress").withStyle(ChatFormatting.GREEN));

            if (maxProgress > 0) {
                float progressPercentage = (float) progress / maxProgress * 100;
                tooltip.add(Component.literal(String.format("%.1f%%", progressPercentage)).withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(Component.literal("0.0%").withStyle(ChatFormatting.GREEN));
            }

            tooltip.add(Component.literal(String.format("Organic Items: %d/%d", organicItems, requiredItems)).withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.agritechevolved.view_recipes").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

            guiGraphics.renderComponentTooltip(this.font, tooltip, x, y);
        }

        if (x >= guiX + 158 && x <= guiX + 158 + 10 && y >= guiY + 15 && y <= guiY + 15 + 52) {
            List<Component> tooltip = new ArrayList<>();
            int energyStored = this.menu.getEnergyStored();
            int maxEnergy = this.menu.getMaxEnergyStored();

            tooltip.add(Component.translatable("tooltip.agritechevolved.stored_energy").withStyle(ChatFormatting.YELLOW));

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            String energyText = numberFormat.format(energyStored) + " / " + numberFormat.format(maxEnergy) + " RF";
            tooltip.add(Component.literal(energyText).withStyle(ChatFormatting.GREEN));

            if (maxEnergy > 0) {
                float energyPercentage = (float) energyStored / maxEnergy * 100;
                tooltip.add(Component.literal(String.format("%.1f%%", energyPercentage)).withStyle(ChatFormatting.GRAY));
            }

            guiGraphics.renderComponentTooltip(this.font, tooltip, x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int guiX = (width - imageWidth) / 2;
            int guiY = (height - imageHeight) / 2;

            if (mouseX >= guiX + 85 && mouseX <= guiX + 85 + 6 && mouseY >= guiY + 15 && mouseY <= guiY + 15 + 52) {
                if (minecraft != null && minecraft.player != null) {
                    IJeiRuntime runtime = ATEJeiPlugin.getJeiRuntime();
                    if (runtime != null) {
                        runtime.getRecipesGui().showTypes(List.of(CompostRecipeCategory.COMPOST_RECIPE_TYPE));
                    }
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}