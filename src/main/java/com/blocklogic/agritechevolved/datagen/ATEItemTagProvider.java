package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.item.ATEItems;
import com.blocklogic.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
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
                .add(ATEItems.BIOMASS.get())
                .add(ATEItems.COMPACTED_BIOMASS.get());

        tag(ATETags.Items.ATE_MODULES)
                .add(ATEItems.SM_MK1.get())
                .add(ATEItems.SM_MK2.get())
                .add(ATEItems.SM_MK3.get())
                .add(ATEItems.PEM_MK1.get())
                .add(ATEItems.PEM_MK2.get())
                .add(ATEItems.PEM_MK3.get())
                .add(ATEItems.YM_MK1.get())
                .add(ATEItems.YM_MK2.get())
                .add(ATEItems.YM_MK3.get())
                .add(ATEItems.FM_MK1.get())
                .add(ATEItems.FM_MK2.get())
                .add(ATEItems.FM_MK3.get());
    }
}
