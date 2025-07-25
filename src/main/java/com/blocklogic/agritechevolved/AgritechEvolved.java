package com.blocklogic.agritechevolved;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import com.blocklogic.agritechevolved.block.entity.*;
import com.blocklogic.agritechevolved.block.entity.renderer.PlanterBlockEntityRenderer;
import com.blocklogic.agritechevolved.command.ATECommands;
import com.blocklogic.agritechevolved.component.ATEDataComponents;
import com.blocklogic.agritechevolved.item.ATECreativeTab;
import com.blocklogic.agritechevolved.item.ATEItems;
import com.blocklogic.agritechevolved.recipe.ATERecipes;
import com.blocklogic.agritechevolved.screen.ATEMenuTypes;
import com.blocklogic.agritechevolved.screen.custom.*;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;


@Mod(AgritechEvolved.MODID)
public class AgritechEvolved
{
    public static final String MODID = "agritechevolved";

    private static final Logger LOGGER = LogUtils.getLogger();

    public AgritechEvolved(IEventBus modEventBus, ModContainer modContainer)
    {

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        ATEBlocks.register(modEventBus);
        ATEItems.register(modEventBus);
        ATECreativeTab.register(modEventBus);
        ATEBlockEntities.register(modEventBus);
        ATEMenuTypes.register(modEventBus);
        ATERecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ATEDataComponents.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(AdvancedPlanterBlockEntity::registerCapabilities);
        modEventBus.addListener(ComposterBlockEntity::registerCapabilities);
        modEventBus.addListener(BiomassBurnerBlockEntity::registerCapabilities);
        modEventBus.addListener(CapacitorBlockEntity::registerCapabilities);


        Config.register(modContainer);

        modEventBus.register(Config.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    public void onRegisterCommands(RegisterCommandsEvent event) {
        ATECommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ATEMenuTypes.BASIC_PLANTER_MENU.get(), BasicPlanterScreen::new);
            event.register(ATEMenuTypes.ADVANCED_PLANTER_MENU.get(), AdvancedPlanterScreen::new);
            event.register(ATEMenuTypes.BURNER_MENU.get(), BiomassBurnerScreen::new);
            event.register(ATEMenuTypes.COMPOSTER_MENU.get(), ComposterScreen::new);
            event.register(ATEMenuTypes.CAPACITOR_MENU.get(), CapacitorScreen::new);
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ATEBlockEntities.BASIC_PLANTER_BE.get(), PlanterBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ATEBlockEntities.ADVANCED_PLANTER_BE.get(), PlanterBlockEntityRenderer::new);
        }
    }
}
