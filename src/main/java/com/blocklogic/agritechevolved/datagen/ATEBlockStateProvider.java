package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.ATEBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ATEBlockStateProvider extends BlockStateProvider {
    public ATEBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AgritechEvolved.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ATEBlocks.MULCH);
        blockWithItem(ATEBlocks.COMPACTED_BIOMASS_BLOCK);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
