package com.blocklogic.agritechevolved.block;

import com.blocklogic.agritechevolved.AgritechEvolved;
import com.blocklogic.agritechevolved.block.custom.*;
import com.blocklogic.agritechevolved.component.ATEDataComponents;
import com.blocklogic.agritechevolved.item.ATEItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ATEBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AgritechEvolved.MODID);

    // Basic Planters
    public static final DeferredBlock<Block> BASIC_PLANTER = registerBlock("basic_planter",
            () -> new BasicPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion())
    );

    public static final DeferredBlock<Block> BASIC_ACACIA_PLANTER = registerBlock("basic_acacia_planter",
            () -> new BasicAcaciaPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_BAMBOO_PLANTER = registerBlock("basic_bamboo_planter",
            () -> new BasicBambooPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_BIRCH_PLANTER = registerBlock("basic_birch_planter",
            () -> new BasicBirchPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_CHERRY_PLANTER = registerBlock("basic_cherry_planter",
            () -> new BasicCherryPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_CRIMSON_PLANTER = registerBlock("basic_crimson_planter",
            () -> new BasicCrimsonPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_DARK_OAK_PLANTER = registerBlock("basic_dark_oak_planter",
            () -> new BasicDarkOakPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_JUNGLE_PLANTER = registerBlock("basic_jungle_planter",
            () -> new BasicJunglePlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_MANGROVE_PLANTER = registerBlock("basic_mangrove_planter",
            () -> new BasicMangrovePlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_SPRUCE_PLANTER = registerBlock("basic_spruce_planter",
            () -> new BasicSprucePlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BASIC_WARPED_PLANTER = registerBlock("basic_warped_planter",
            () -> new BasicWarpedPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> ADVANCED_PLANTER = registerBlock("advanced_planter",
            () -> new AdvancedPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            );

    public static final DeferredBlock<Block> COMPOSTER = registerBlock("composter",
            () -> new ComposterBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    public static final DeferredBlock<Block> BIOMASS_BURNER = registerBlock("biomass_burner",
            () -> new BiomassBurnerBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    public static final DeferredBlock<Block> CAPACITOR_TIER1 = registerBlock("capacitor_tier1",
            () -> new CapacitorTier1Block(BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);

                    Integer storedEnergy = stack.get(ATEDataComponents.STORED_ENERGY.get());
                    if (storedEnergy != null && storedEnergy > 0) {
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy",
                                numberFormat.format(storedEnergy)).withStyle(ChatFormatting.YELLOW));
                    }
                }
            }
    );

    public static final DeferredBlock<Block> CAPACITOR_TIER2 = registerBlock("capacitor_tier2",
            () -> new CapacitorTier2Block(BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);

                    Integer storedEnergy = stack.get(ATEDataComponents.STORED_ENERGY.get());
                    if (storedEnergy != null && storedEnergy > 0) {
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy",
                                numberFormat.format(storedEnergy)).withStyle(ChatFormatting.YELLOW));
                    }
                }
            }
    );

    public static final DeferredBlock<Block> CAPACITOR_TIER3 = registerBlock("capacitor_tier3",
            () -> new CapacitorTier3Block(BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);

                    Integer storedEnergy = stack.get(ATEDataComponents.STORED_ENERGY.get());
                    if (storedEnergy != null && storedEnergy > 0) {
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy",
                                numberFormat.format(storedEnergy)).withStyle(ChatFormatting.YELLOW));
                    }
                }
            }
    );

    public static final DeferredBlock<Block> COMPACTED_BIOMASS_BLOCK = registerBlock("compacted_biomass_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    public static final DeferredBlock<Block> INFUSED_FARMLAND = registerBlock("infused_farmland",
            () -> new InfusedFarmlandBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F, 2.0F)
                    .sound(SoundType.GRAVEL)
                    .noOcclusion()
                    .randomTicks())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.infused_farmland"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    public static final DeferredBlock<Block> MULCH = registerBlock("mulch",
            () -> new MulchBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F, 2.0F)
                    .sound(SoundType.GRAVEL))
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.mulch"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            }
    );

    private static <T extends Block>DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ATEItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
