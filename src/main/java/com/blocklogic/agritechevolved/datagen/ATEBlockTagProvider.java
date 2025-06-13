package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.util.ATETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ATEBlockTagProvider extends BlockTagsProvider {
    public ATEBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AgritechEvolved.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ATEBlocks.ADVANCED_PLANTER.get())
                .add(ATEBlocks.COMPOSTER.get())
                .add(ATEBlocks.INFUSER.get())
                .add(ATEBlocks.BIOMASS_BURNER.get())
                .add(ATEBlocks.CAPACITOR_TIER1.get())
                .add(ATEBlocks.CAPACITOR_TIER2.get())
                .add(ATEBlocks.CAPACITOR_TIER3.get())
                .add(ATEBlocks.COMPACTED_BIOMASS_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ATEBlocks.INFUSED_FARMLAND.get())
                .add(ATEBlocks.MULCH.get());

        tag(ATETags.Blocks.PLANTER)
                .add(ATEBlocks.ADVANCED_PLANTER.get());

        tag(ATETags.Blocks.MACHINES)
                .add(ATEBlocks.COMPOSTER.get())
                .add(ATEBlocks.INFUSER.get())
                .add(ATEBlocks.BIOMASS_BURNER.get())
                .add(ATEBlocks.CAPACITOR_TIER1.get())
                .add(ATEBlocks.CAPACITOR_TIER2.get())
                .add(ATEBlocks.CAPACITOR_TIER3.get());

        tag(ATETags.Blocks.NATURAL_BLOCKS)
                .add(ATEBlocks.INFUSED_FARMLAND.get())
                .add(ATEBlocks.MULCH.get());
    }
}
