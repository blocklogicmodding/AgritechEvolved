package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.item.ATEItems;
import com.blocklogic.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ATEItemTagProvider extends ItemTagsProvider {
    public ATEItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, AgritechEvolved.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ATETags.Items.BIOMASS)
                .add(ATEItems.CRUDE_BIOMASS.get())
                .add(ATEItems.BIOMASS.get())
                .add(ATEItems.COMPACTED_BIOMASS.get());

        tag(ATETags.Items.ATE_MODULES)
                .add(ATEItems.SM_MK1.get())
                .add(ATEItems.SM_MK2.get())
                .add(ATEItems.SM_MK3.get())
                .add(ATEItems.YM_MK1.get())
                .add(ATEItems.YM_MK2.get())
                .add(ATEItems.YM_MK3.get());

        copy(ATETags.Blocks.DIRT_LIKE_BLOCKS, ATETags.Items.DIRT_LIKE_BLOCK_ITEMS);
        copy(ATETags.Blocks.BASIC_PLANTERS, ATETags.Items.BASIC_PLANTER_ITEMS);
    }
}
