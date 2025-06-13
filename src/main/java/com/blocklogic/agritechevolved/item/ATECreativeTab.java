package com.blocklogic.agritechevolved.item;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.ATEBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ATECreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AgritechEvolved.MODID);

    public static final Supplier<CreativeModeTab> AGRITECHEVOLVED = CREATIVE_MODE_TAB.register("agritechevolved",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ATEBlocks.ADVANCED_PLANTER.get()))
                    .title(Component.translatable("creativetab.agritechevolved.agritechevolved"))
                    .displayItems((ItemDisplayParameters, output) -> {

                        output.accept(ATEBlocks.ADVANCED_PLANTER);
                        output.accept(ATEBlocks.COMPOSTER);
                        output.accept(ATEBlocks.INFUSER);
                        output.accept(ATEBlocks.BIOMASS_BURNER);
                        output.accept(ATEBlocks.CAPACITOR_TIER1);
                        output.accept(ATEBlocks.CAPACITOR_TIER2);
                        output.accept(ATEBlocks.CAPACITOR_TIER3);
                        output.accept(ATEBlocks.COMPACTED_BIOMASS_BLOCK);
                        output.accept(ATEBlocks.INFUSED_FARMLAND);
                        output.accept(ATEBlocks.MULCH);

                        output.accept(ATEItems.SM_MK1);
                        output.accept(ATEItems.SM_MK2);
                        output.accept(ATEItems.SM_MK3);
                        output.accept(ATEItems.PEM_MK1);
                        output.accept(ATEItems.PEM_MK2);
                        output.accept(ATEItems.PEM_MK3);
                        output.accept(ATEItems.YM_MK1);
                        output.accept(ATEItems.YM_MK2);
                        output.accept(ATEItems.YM_MK3);
                        output.accept(ATEItems.BIOMASS);
                        output.accept(ATEItems.COMPACTED_BIOMASS);
                    })
                    .build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
