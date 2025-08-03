package com.blocklogic.agritechevolved.compat.jade;

import com.blocklogic.agritechevolved.block.custom.AdvancedPlanterBlock;
import com.blocklogic.agritechevolved.block.custom.BasicPlanterBlock;
import com.blocklogic.agritechevolved.block.entity.AdvancedPlanterBlockEntity;
import com.blocklogic.agritechevolved.block.entity.BasicPlanterBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ATEJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(PlanterProvider.INSTANCE, BasicPlanterBlockEntity.class);
        registration.registerBlockDataProvider(PlanterProvider.INSTANCE, AdvancedPlanterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(PlanterProvider.INSTANCE, BasicPlanterBlock.class);
        registration.registerBlockComponent(PlanterProvider.INSTANCE, AdvancedPlanterBlock.class);
    }
}
