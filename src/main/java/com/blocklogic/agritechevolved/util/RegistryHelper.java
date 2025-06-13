package com.blocklogic.agritechevolved.util;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

public class RegistryHelper {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static String getItemId(Item item) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
        return registryName.toString();
    }

    public static String getItemId(ItemStack stack) {
        return getItemId(stack.getItem());
    }

    public static String getBlockId(Block block) {
        ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);
        return registryName.toString();
    }

    public static Item getItem(String id) {
        try {
            ResourceLocation resourceLocation = ResourceLocation.parse(id);
            if (BuiltInRegistries.ITEM.containsKey(resourceLocation)) {
                return BuiltInRegistries.ITEM.get(resourceLocation);
            }
        } catch (Exception e){
            LOGGER.error("Invalid item ID in config: {}", id, e);
        }
        return null;
    }

    public static Block getBlock(String id) {
        try {
            ResourceLocation resourceLocation = ResourceLocation.parse(id);
            if (BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
                return BuiltInRegistries.BLOCK.get(resourceLocation);
            }
        } catch (Exception e) {
            LOGGER.error("Invalid block ID in config: {}", id, e);
        }
        return null;
    }

}