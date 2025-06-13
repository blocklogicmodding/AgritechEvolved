package com.blocklogic.agritechevolved.item;

import com.blocklogic.agritechevolved.AgritechEvolved;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ATEItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AgritechEvolved.MODID);

    public static final DeferredItem<Item> SM_MK1 = ITEMS.register("sm_mk1",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.sm_mk1_shiftdown"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.sm_mk1"));
                    }

                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> SM_MK2 = ITEMS.register("sm_mk2",
            () -> new Item(new Item.Properties())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.sm_mk2_shiftdown"));
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
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.sm_mk3_shiftdown"));
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
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.ym_mk1_shiftdown"));
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
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.ym_mk2_shiftdown"));
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
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.ym_mk3_shiftdown"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.ym_mk3"));
                    }

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
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
