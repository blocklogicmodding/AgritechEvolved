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

public class CapacitorScreen extends AbstractContainerScreen<CapacitorMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "textures/gui/capacitor_gui.png");

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY -= 2;
    }

    public CapacitorScreen(CapacitorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 151;
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int energyStored = this.menu.getEnergyStored();
        int maxEnergyStored = this.menu.getMaxEnergyStored();

        if (maxEnergyStored > 0) {
            int fillWidth = (int) (160.0 * energyStored / maxEnergyStored);
            if (fillWidth > 0) {
                guiGraphics.blit(GUI_TEXTURE, x + 8, y + 15, 0, 151, fillWidth, 34);
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

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 15 && mouseY <= y + 49) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            List<Component> tooltip = new ArrayList<>();

            int energyStored = this.menu.getEnergyStored();
            int maxEnergyStored = this.menu.getMaxEnergyStored();
            int transferRate = this.menu.getTransferRate();
            String tierName = this.menu.getTierName();

            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.title", tierName)
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.energy_storage")
                    .withStyle(ChatFormatting.YELLOW));

            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.energy",
                            numberFormat.format(energyStored), numberFormat.format(maxEnergyStored))
                    .withStyle(ChatFormatting.WHITE));

            double percentage = maxEnergyStored > 0 ? (energyStored * 100.0 / maxEnergyStored) : 0;
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.energy_percentage",
                    String.format("%.1f", percentage)).withStyle(ChatFormatting.GRAY));

            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.transfer_rate",
                    numberFormat.format(transferRate)).withStyle(ChatFormatting.AQUA));

            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.connects_all_sides")
                    .withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.except_bottom")
                    .withStyle(ChatFormatting.DARK_GREEN));

            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 6 && mouseY <= y + 14) {
            List<Component> tooltip = new ArrayList<>();
            String tierName = this.menu.getTierName();
            int transferRate = this.menu.getTransferRate();

            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.title", tierName)
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.description")
                    .withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.max_transfer",
                            NumberFormat.getNumberInstance(Locale.US).format(transferRate))
                    .withStyle(ChatFormatting.AQUA));

            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if (mouseX >= x + 8 && mouseX <= x + 168 && mouseY >= y + 50 && mouseY <= y + 65) {
            List<Component> tooltip = new ArrayList<>();

            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.information")
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.info_stores")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.info_balances")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.info_automatic")
                    .withStyle(ChatFormatting.GRAY));

            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);

        int energyStored = this.menu.getEnergyStored();
        int maxEnergyStored = this.menu.getMaxEnergyStored();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        Component energyText = Component.literal(numberFormat.format(energyStored) + " RF");
        int energyWidth = this.font.width(energyText);
        int bufferCenterX = 8 + (160 / 2);
        int bufferCenterY = 15 + (34 / 2) - 8;

        guiGraphics.drawString(this.font, energyText, bufferCenterX - (energyWidth / 2), bufferCenterY, 0xFFFFFF, true);

        if (maxEnergyStored > 0) {
            double percentage = (energyStored * 100.0 / maxEnergyStored);
            Component percentText = Component.literal(String.format("%.1f%%", percentage));
            int percentWidth = this.font.width(percentText);
            guiGraphics.drawString(this.font, percentText, bufferCenterX - (percentWidth / 2), bufferCenterY + 10, 0xCCCCCC, true);
        }
    }
}