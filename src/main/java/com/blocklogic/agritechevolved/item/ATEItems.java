package com.blocklogic.agritechevolved.item;

import com.blocklogic.agritechevolved.AgritechEvolved;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ATEItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AgritechEvolved.MODID);

    public static final DeferredItem<Item> SM_MK1 = ITEMS.register("sm_mk1",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SM_MK2 = ITEMS.register("sm_mk2",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SM_MK3 = ITEMS.register("sm_mk3",
            () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> PEM_MK1 = ITEMS.register("pem_mk1",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PEM_MK2 = ITEMS.register("pem_mk2",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PEM_MK3 = ITEMS.register("pem_mk3",
            () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> YM_MK1 = ITEMS.register("ym_mk1",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> YM_MK2 = ITEMS.register("ym_mk2",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> YM_MK3 = ITEMS.register("ym_mk3",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BIOMASS = ITEMS.register("biomass",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COMPACTED_BIOMASS = ITEMS.register("compacted_biomass",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
