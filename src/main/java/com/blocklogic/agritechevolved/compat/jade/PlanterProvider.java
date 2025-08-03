package com.blocklogic.agritechevolved.compat.jade;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.entity.AdvancedPlanterBlockEntity;
import com.blocklogic.agritechevolved.block.entity.BasicPlanterBlockEntity;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum PlanterProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AgritechEvolved.MODID, "planter_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        if (!data.contains("hasCrop") || !data.getBoolean("hasCrop")) {
            return;
        }

        String cropName = data.getString("cropName");
        int currentStage = data.getInt("currentStage");
        int maxStage = data.getInt("maxStage");
        float progressPercent = data.getFloat("progressPercent");
        String soilName = data.getString("soilName");
        float growthModifier = data.getFloat("growthModifier");
        boolean isAdvanced = data.getBoolean("isAdvanced");

        // Show growth status
        if (progressPercent >= 100.0f) {
            tooltip.add(Component.translatable("jade.agritechevolved.crop_ready", cropName)
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        } else {
            tooltip.add(Component.translatable("jade.agritechevolved.crop_progress",
                            cropName, currentStage, maxStage, Math.round(progressPercent))
                    .withStyle(ChatFormatting.DARK_GREEN));
        }

        // Show soil information
        tooltip.add(Component.translatable("jade.agritechevolved.soil_info",
                        soilName, String.format("%.2fx", growthModifier))
                .withStyle(ChatFormatting.GRAY));

        // Advanced Planter specific information
        if (isAdvanced) {
            if (data.contains("energyStored") && data.contains("maxEnergy")) {
                int energyStored = data.getInt("energyStored");
                int maxEnergy = data.getInt("maxEnergy");
                float energyPercent = maxEnergy > 0 ? (energyStored / (float) maxEnergy) * 100 : 0;

                tooltip.add(Component.translatable("jade.agritechevolved.energy_info",
                                String.format("%,d", energyStored), String.format("%,d", maxEnergy), Math.round(energyPercent))
                        .withStyle(ChatFormatting.GOLD));
            }

            // Show modules if any are installed
            if (data.contains("hasModules") && data.getBoolean("hasModules")) {
                String moduleInfo = data.getString("moduleInfo");
                tooltip.add(Component.translatable("jade.agritechevolved.modules_installed", moduleInfo)
                        .withStyle(ChatFormatting.BLUE));
            }

            // Show fertilizer if present
            if (data.contains("hasFertilizer") && data.getBoolean("hasFertilizer")) {
                String fertilizerName = data.getString("fertilizerName");
                tooltip.add(Component.translatable("jade.agritechevolved.fertilizer_info", fertilizerName)
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        // Handle both Basic and Advanced Planters
        if (accessor.getBlockEntity() instanceof BasicPlanterBlockEntity basicPlanter) {
            appendBasicPlanterData(data, basicPlanter);
        } else if (accessor.getBlockEntity() instanceof AdvancedPlanterBlockEntity advancedPlanter) {
            appendAdvancedPlanterData(data, advancedPlanter);
        }
    }

    private void appendBasicPlanterData(CompoundTag data, BasicPlanterBlockEntity planter) {
        ItemStack seedStack = planter.inventory.getStackInSlot(0);
        ItemStack soilStack = planter.inventory.getStackInSlot(1);

        data.putBoolean("isAdvanced", false);

        if (!seedStack.isEmpty() && !soilStack.isEmpty()) {
            data.putBoolean("hasCrop", true);
            data.putString("cropName", seedStack.getDisplayName().getString());
            data.putInt("currentStage", planter.getGrowthStage());
            data.putInt("maxStage", getMaxStage(seedStack));
            data.putFloat("progressPercent", planter.getGrowthProgress() * 100);
            data.putString("soilName", soilStack.getDisplayName().getString());
            data.putFloat("growthModifier", planter.getSoilGrowthModifier(soilStack));
        } else {
            data.putBoolean("hasCrop", false);
        }
    }

    private void appendAdvancedPlanterData(CompoundTag data, AdvancedPlanterBlockEntity planter) {
        ItemStack seedStack = planter.inventory.getStackInSlot(0);
        ItemStack soilStack = planter.inventory.getStackInSlot(1);

        data.putBoolean("isAdvanced", true);

        if (!seedStack.isEmpty() && !soilStack.isEmpty()) {
            data.putBoolean("hasCrop", true);
            data.putString("cropName", seedStack.getDisplayName().getString());
            data.putInt("currentStage", planter.getGrowthStage());
            data.putInt("maxStage", getMaxStage(seedStack));
            data.putFloat("progressPercent", planter.getGrowthProgress() * 100);
            data.putString("soilName", soilStack.getDisplayName().getString());
            data.putFloat("growthModifier", planter.getSoilGrowthModifier(soilStack));

            // Module information
            StringBuilder moduleInfo = new StringBuilder();
            boolean hasModules = false;

            // Check module slots (slots 2 and 3)
            for (int slot = 2; slot <= 3; slot++) {
                ItemStack moduleStack = planter.inventory.getStackInSlot(slot);
                if (!moduleStack.isEmpty()) {
                    if (hasModules) {
                        moduleInfo.append(", ");
                    }
                    moduleInfo.append(moduleStack.getDisplayName().getString());
                    hasModules = true;
                }
            }

            data.putBoolean("hasModules", hasModules);
            if (hasModules) {
                data.putString("moduleInfo", moduleInfo.toString());
            }

            // Fertilizer information (slot 4)
            ItemStack fertilizerStack = planter.inventory.getStackInSlot(4);
            if (!fertilizerStack.isEmpty()) {
                data.putBoolean("hasFertilizer", true);
                data.putString("fertilizerName", fertilizerStack.getDisplayName().getString());
            } else {
                data.putBoolean("hasFertilizer", false);
            }
        } else {
            data.putBoolean("hasCrop", false);
        }
    }

    private int getMaxStage(ItemStack plantStack) {
        String itemId = RegistryHelper.getItemId(plantStack);

        // Trees have different max stages than crops
        if (PlantablesConfig.isValidSapling(itemId)) {
            return 1; // Trees typically have just 2 stages: growing and mature
        } else {
            return 8; // Crops typically have 8 growth stages (0-7, display as 1-8)
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}