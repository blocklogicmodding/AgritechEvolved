package com.blocklogic.agritechevolved.datagen;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.item.ATEItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ATEItemModelProvider extends ItemModelProvider {
    public ATEItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AgritechEvolved.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ATEItems.SM_MK1.get());
        basicItem(ATEItems.SM_MK2.get());
        basicItem(ATEItems.SM_MK3.get());
        basicItem(ATEItems.YM_MK1.get());
        basicItem(ATEItems.YM_MK2.get());
        basicItem(ATEItems.YM_MK3.get());
        basicItem(ATEItems.CRUDE_BIOMASS.get());
        basicItem(ATEItems.BIOMASS.get());
        basicItem(ATEItems.COMPACTED_BIOMASS.get());
    }
}
