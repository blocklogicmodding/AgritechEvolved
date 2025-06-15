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
                        tooltipComponents.add(Component.literal(
                                "§aBoosts Advanced Planter and Machine processing speed by " + speedBoost + "%§r, " +
                                        "§cbut increases power consumption by " + powerIncrease + "%.§r"
                        ));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.sm_mk1"));
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
                        tooltipComponents.add(Component.literal(
                                "§aBoosts Advanced Planter and Machine processing speed by " + speedBoost + "%§r, " +
                                        "§cbut increases power consumption by " + powerIncrease + "%.§r"
                        ));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.sm_mk2"));
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
                        tooltipComponents.add(Component.literal(
                                "§aBoosts Advanced Planter and Machine processing speed by " + speedBoost + "%§r, " +
                                        "§cbut increases power consumption by " + powerIncrease + "%.§r"
                        ));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.sm_mk3"));
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
                        tooltipComponents.add(Component.literal(
                                "§aBoosts Advanced Planter yield by " + yieldBoost + "%§r, " +
                                        "§cbut reduces processing speed by " + speedReduction + "%.§r"
                        ));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.ym_mk1"));
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
                        tooltipComponents.add(Component.literal(
                                "§aBoosts Advanced Planter yield by " + yieldBoost + "%§r, " +
                                        "§cbut reduces processing speed by " + speedReduction + "%.§r"
                        ));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.ym_mk2"));
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
                        tooltipComponents.add(Component.literal(
                                "§aBoosts Advanced Planter yield by " + yieldBoost + "%§r, " +
                                        "§cbut reduces processing speed by " + speedReduction + "%.§r"
                        ));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.ym_mk3"));
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
                    int rfValue = Config.getBurnerCrudeBiomassRfValue();
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_biomass.rf_generation",
                            numberFormat.format(rfValue)).withStyle(ChatFormatting.GREEN));

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
                    int rfValue = Config.getBurnerBiomassRfValue();
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.biomass.rf_generation",
                            numberFormat.format(rfValue)).withStyle(ChatFormatting.GREEN));

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
                    int rfValue = Config.getBurnerCompactedBiomassRfValue();
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.compacted_biomass.rf_generation",
                            numberFormat.format(rfValue)).withStyle(ChatFormatting.GREEN));

                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}