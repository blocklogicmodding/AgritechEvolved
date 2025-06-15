package com.blocklogic.agritechevolved.item;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ATEItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AgritechEvolved.MODID);

    public static final DeferredItem<Item> SM_MK1 = ITEMS.register("sm_mk1",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int speedBoost = (int) Math.round((Config.getSpeedModuleMk1Multiplier() - 1.0) * 100);
                        int powerIncrease = (int) Math.round((Config.getSpeedModuleMk1PowerMultiplier() - 1.0) * 100);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_boost", speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.power_increase", powerIncrease));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> SM_MK2 = ITEMS.register("sm_mk2",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int speedBoost = (int) Math.round((Config.getSpeedModuleMk2Multiplier() - 1.0) * 100);
                        int powerIncrease = (int) Math.round((Config.getSpeedModuleMk2PowerMultiplier() - 1.0) * 100);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_boost", speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.power_increase", powerIncrease));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> SM_MK3 = ITEMS.register("sm_mk3",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int speedBoost = (int) Math.round((Config.getSpeedModuleMk3Multiplier() - 1.0) * 100);
                        int powerIncrease = (int) Math.round((Config.getSpeedModuleMk3PowerMultiplier() - 1.0) * 100);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_boost", speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.power_increase", powerIncrease));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> YM_MK1 = ITEMS.register("ym_mk1",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int yieldBoost = (int) Math.round((Config.getYieldModuleMk1Multiplier() - 1.0) * 100);
                        int speedReduction = (int) Math.round((1.0 - Config.getYieldModuleMk1SpeedPenalty()) * 100);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.yield_boost", yieldBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_reduction", speedReduction));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> YM_MK2 = ITEMS.register("ym_mk2",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int yieldBoost = (int) Math.round((Config.getYieldModuleMk2Multiplier() - 1.0) * 100);
                        int speedReduction = (int) Math.round((1.0 - Config.getYieldModuleMk2SpeedPenalty()) * 100);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.yield_boost", yieldBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_reduction", speedReduction));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> YM_MK3 = ITEMS.register("ym_mk3",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        int yieldBoost = (int) Math.round((Config.getYieldModuleMk3Multiplier() - 1.0) * 100);
                        int speedReduction = (int) Math.round((1.0 - Config.getYieldModuleMk3SpeedPenalty()) * 100);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.yield_boost", yieldBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.speed_reduction", speedReduction));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.module.shift_info"));
                    }
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> CRUDE_BIOMASS = ITEMS.register("crude_biomass",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_biomass"));

                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                    int baseRF = Config.getBurnerCrudeBiomassRfValue();
                    int burnDuration = Config.getBurnerCrudeBiomassBurnDuration();
                    int baseDuration = 50;
                    int actualRF = (int) (baseRF * ((float) burnDuration / baseDuration));

                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_biomass.rf_generation",
                            numberFormat.format(actualRF)).withStyle(ChatFormatting.GREEN));

                    if (Screen.hasShiftDown()) {
                        double burnSeconds = burnDuration / 20.0;
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.burn_duration",
                                String.format("%.1f", burnSeconds)).withStyle(ChatFormatting.AQUA));

                        int rfPerSecond = (int) Math.round(actualRF / burnSeconds);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.rf_per_second",
                                numberFormat.format(rfPerSecond)).withStyle(ChatFormatting.YELLOW));

                        if (burnDuration != baseDuration) {
                            float multiplier = (float) burnDuration / baseDuration;
                            int multiplierPercent = (int) Math.round((multiplier - 1.0) * 100);
                            if (multiplierPercent > 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_bonus",
                                        multiplierPercent).withStyle(ChatFormatting.GREEN));
                            } else if (multiplierPercent < 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_penalty",
                                        Math.abs(multiplierPercent)).withStyle(ChatFormatting.RED));
                            }
                        }
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_fuel.shift_info"));
                    }

                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_biomass.inefficient")
                            .withStyle(ChatFormatting.RED));

                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> BIOMASS = ITEMS.register("biomass",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.biomass"));

                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                    int baseRF = Config.getBurnerBiomassRfValue();
                    int burnDuration = Config.getBurnerBiomassBurnDuration();
                    int baseDuration = 100;
                    int actualRF = (int) (baseRF * ((float) burnDuration / baseDuration));

                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.biomass.rf_generation",
                                numberFormat.format(actualRF)).withStyle(ChatFormatting.GREEN));

                        double burnSeconds = burnDuration / 20.0;
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.burn_duration",
                                String.format("%.1f", burnSeconds)).withStyle(ChatFormatting.AQUA));

                        int rfPerSecond = (int) Math.round(actualRF / burnSeconds);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.rf_per_second",
                                numberFormat.format(rfPerSecond)).withStyle(ChatFormatting.YELLOW));

                        if (burnDuration != baseDuration) {
                            float multiplier = (float) burnDuration / baseDuration;
                            int multiplierPercent = (int) Math.round((multiplier - 1.0) * 100);
                            if (multiplierPercent > 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_bonus",
                                        multiplierPercent).withStyle(ChatFormatting.GREEN));
                            } else if (multiplierPercent < 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_penalty",
                                        Math.abs(multiplierPercent)).withStyle(ChatFormatting.RED));
                            }
                        }

                        int speedBoost = (int) Math.round((Config.getFertilizerBiomassSpeedMultiplier() - 1.0) * 100);
                        int yieldBoost = (int) Math.round((Config.getFertilizerBiomassYieldMultiplier() - 1.0) * 100);
                        tooltipComponents.add(Component.literal(""));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fertilizer.effects")
                                .withStyle(ChatFormatting.GOLD));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fertilizer.speed_boost",
                                speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fertilizer.yield_boost",
                                yieldBoost));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.shift_info"));
                    }

                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredItem<Item> COMPACTED_BIOMASS = ITEMS.register("compacted_biomass",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.compacted"));

                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                    int baseRF = Config.getBurnerCompactedBiomassRfValue();
                    int burnDuration = Config.getBurnerCompactedBiomassBurnDuration();
                    int baseDuration = 180;
                    int actualRF = (int) (baseRF * ((float) burnDuration / baseDuration));

                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.compacted_biomass.rf_generation",
                                numberFormat.format(actualRF)).withStyle(ChatFormatting.GREEN));

                        double burnSeconds = burnDuration / 20.0;
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.burn_duration",
                                String.format("%.1f", burnSeconds)).withStyle(ChatFormatting.AQUA));

                        int rfPerSecond = (int) Math.round(actualRF / burnSeconds);
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.rf_per_second",
                                numberFormat.format(rfPerSecond)).withStyle(ChatFormatting.YELLOW));

                        if (burnDuration != baseDuration) {
                            float multiplier = (float) burnDuration / baseDuration;
                            int multiplierPercent = (int) Math.round((multiplier - 1.0) * 100);
                            if (multiplierPercent > 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_bonus",
                                        multiplierPercent).withStyle(ChatFormatting.GREEN));
                            } else if (multiplierPercent < 0) {
                                tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.duration_penalty",
                                        Math.abs(multiplierPercent)).withStyle(ChatFormatting.RED));
                            }
                        }

                        int speedBoost = (int) Math.round((Config.getFertilizerCompactedBiomassSpeedMultiplier() - 1.0) * 100);
                        int yieldBoost = (int) Math.round((Config.getFertilizerCompactedBiomassYieldMultiplier() - 1.0) * 100);
                        tooltipComponents.add(Component.literal(""));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fertilizer.effects")
                                .withStyle(ChatFormatting.GOLD));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fertilizer.speed_boost",
                                speedBoost));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fertilizer.yield_boost",
                                yieldBoost));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.shift_info"));
                    }

                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}