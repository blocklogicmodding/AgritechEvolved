package com.blocklogic.agritechevolved.screen.custom;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.mojang.blaze3d.systems.RenderSystem;
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

public class AdvancedPlanterScreen extends AbstractContainerScreen<AdvancedPlanterMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "textures/gui/advanced_planter_gui.png");

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public AdvancedPlanterScreen(AdvancedPlanterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 170;
        this.imageWidth = 212;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 26;

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        float growthProgress = this.menu.blockEntity.getGrowthProgress();
        if (growthProgress > 0) {
            int progressBarHeight = (int) (52 * growthProgress);
            int progressBarY = y + 16 + 52 - progressBarHeight;

            guiGraphics.blit(GUI_TEXTURE,
                    x + 40, progressBarY,
                    222, 52 - progressBarHeight,
                    6, progressBarHeight
            );
        }

        int energyStored = this.menu.blockEntity.getEnergyStored();
        int maxEnergy = this.menu.blockEntity.getMaxEnergyStored();
        if (maxEnergy > 0) {
            float energyPercentage = (float) energyStored / maxEnergy;
            int energyBarHeight = (int) (52 * energyPercentage);
            int energyBarY = y + 16 + 52 - energyBarHeight;

            guiGraphics.blit(GUI_TEXTURE,
                    x + 194, energyBarY,
                    212, 52 - energyBarHeight,
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

        if (x >= guiX + 40 && x <= guiX + 40 + 6 && y >= guiY + 16 && y <= guiY + 16 + 53) {
            List<Component> tooltip = new ArrayList<>();
            float progress = this.menu.blockEntity.getGrowthProgress();
            tooltip.add(Component.translatable("tooltip.agritechevolved.growth_progress"));
            tooltip.add(Component.literal(String.format("%.1f%%", progress * 100)));
            tooltip.add(Component.translatable("tooltip.agritechevolved.view_recipes").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            guiGraphics.renderComponentTooltip(this.font, tooltip, x, y);
        }

        if (x >= guiX + 194 && x <= guiX + 194 + 10 && y >= guiY + 16 && y <= guiY + 16 + 53) {
            List<Component> tooltip = new ArrayList<>();
            int energyStored = this.menu.blockEntity.getEnergyStored();
            int maxEnergy = this.menu.blockEntity.getMaxEnergyStored();

            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

            tooltip.add(Component.translatable("tooltip.agritechevolved.stored_energy"));
            tooltip.add(Component.literal(formatter.format(energyStored) + " / " + formatter.format(maxEnergy) + " RF"));

            if (maxEnergy > 0) {
                float percentage = ((float) energyStored / maxEnergy) * 100;
                tooltip.add(Component.literal(String.format("%.1f%%", percentage)));
            }

            guiGraphics.renderComponentTooltip(this.font, tooltip, x, y);
        }
    }
}