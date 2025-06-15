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

public class BiomassBurnerScreen extends AbstractContainerScreen<BiomassBurnerMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "textures/gui/burner_gui.png");

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public BiomassBurnerScreen(BiomassBurnerMenu menu, Inventory playerInventory, Component title) {
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
            int progressWidth = (int) (52.0 * progress / maxProgress);
            if (progressWidth > 0) {
                guiGraphics.blit(GUI_TEXTURE, x + 62, y + 59, 0, 166, progressWidth, 6);
            }
        }

        int energyStored = this.menu.getEnergyStored();
        int maxEnergyStored = this.menu.getMaxEnergyStored();
        if (maxEnergyStored > 0) {
            int energyHeight = (int) (52.0 * energyStored / maxEnergyStored);
            if (energyHeight > 0) {
                int startY = y + 15 + (52 - energyHeight);
                guiGraphics.blit(GUI_TEXTURE, x + 158, startY, 176, 52 - energyHeight, 10, energyHeight);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 158 && mouseX <= x + 168 && mouseY >= y + 15 && mouseY <= y + 67) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            List<Component> tooltip = new ArrayList<>();

            int energyStored = this.menu.getEnergyStored();
            int maxEnergyStored = this.menu.getMaxEnergyStored();

            tooltip.add(Component.translatable("tooltip.agritechevolved.burner.energy",
                            numberFormat.format(energyStored), numberFormat.format(maxEnergyStored))
                    .withStyle(ChatFormatting.YELLOW));

            double percentage = maxEnergyStored > 0 ? (energyStored * 100.0 / maxEnergyStored) : 0;
            tooltip.add(Component.translatable("tooltip.agritechevolved.burner.energy_percentage",
                    String.format("%.1f", percentage)).withStyle(ChatFormatting.GRAY));

            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if (mouseX >= x + 62 && mouseX <= x + 114 && mouseY >= y + 58 && mouseY <= y + 64) {
            List<Component> tooltip = new ArrayList<>();

            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();

            if (maxProgress > 0) {
                double percentage = (progress * 100.0 / maxProgress);
                int remainingTicks = maxProgress - progress;
                double remainingSeconds = remainingTicks / 20.0;

                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.burning_progress")
                        .withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.progress_percentage",
                        String.format("%.1f", percentage)).withStyle(ChatFormatting.YELLOW));
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.time_remaining",
                        String.format("%.1f", remainingSeconds)).withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.no_fuel")
                        .withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.insert_fuel")
                        .withStyle(ChatFormatting.GRAY));
            }

            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if (mouseX >= x + 79 && mouseX <= x + 97 && mouseY >= y + 31 && mouseY <= y + 49) {
            if (this.menu.blockEntity.inventory.getStackInSlot(0).isEmpty()) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.fuel_slot")
                        .withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.accepts")
                        .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.accepts_biomass")
                        .withStyle(ChatFormatting.GREEN));
                tooltip.add(Component.translatable("tooltip.agritechevolved.burner.accepts_compacted_biomass")
                        .withStyle(ChatFormatting.GREEN));

                guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
            }
        }
    }
}