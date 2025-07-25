package com.blocklogic.agritechevolved.block.custom;

import com.blocklogic.agritechevolved.block.entity.ATEBlockEntities;
import com.blocklogic.agritechevolved.block.entity.BasicPlanterBlockEntity;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.screen.custom.BasicPlanterMenu;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BasicPlanterBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(1.0, 0, 1.0, 15, 11, 15);
    public static final MapCodec<BasicPlanterBlock> CODEC = simpleCodec(BasicPlanterBlock::new);

    public BasicPlanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicPlanterBlockEntity(blockPos, blockState);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BasicPlanterBlockEntity planterBlockEntity) {
                planterBlockEntity.drops();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof BasicPlanterBlockEntity basicPlanterBlockEntity) {
            if (player.isCrouching()) {
                if (!level.isClientSide()) {
                    MenuProvider menuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory, playerEntity) ->
                                    new BasicPlanterMenu(containerId, playerInventory, basicPlanterBlockEntity),
                            Component.translatable("gui.agritechevolved.basic_planter")
                    );

                    player.openMenu(menuProvider, pos);
                }
                return ItemInteractionResult.SUCCESS;
            }

            ItemStack heldItem = player.getItemInHand(hand);
            String heldItemId = RegistryHelper.getItemId(heldItem);

            if (PlantablesConfig.isValidSeed(heldItemId) || PlantablesConfig.isValidSapling(heldItemId)) {
                if (level.isClientSide()) {
                    return ItemInteractionResult.SUCCESS;
                }

                if (basicPlanterBlockEntity.inventory.getStackInSlot(0).isEmpty()) {
                    ItemStack soilStack = basicPlanterBlockEntity.inventory.getStackInSlot(1);
                    if (!soilStack.isEmpty()) {
                        String soilId = RegistryHelper.getItemId(soilStack);
                        boolean validCombination = false;

                        if (PlantablesConfig.isValidSeed(heldItemId)) {
                            validCombination = PlantablesConfig.isSoilValidForSeed(soilId, heldItemId);
                        } else if (PlantablesConfig.isValidSapling(heldItemId)) {
                            validCombination = PlantablesConfig.isSoilValidForSapling(soilId, heldItemId);
                        }

                        if (!validCombination) {
                            player.displayClientMessage(Component.translatable("message.agritechevolved.invalid_plant_soil_combination"), true);
                            return ItemInteractionResult.SUCCESS;
                        }
                    }

                    ItemStack plantStack = heldItem.copyWithCount(1);
                    basicPlanterBlockEntity.inventory.setStackInSlot(0, plantStack);
                    heldItem.shrink(1);
                    level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);

                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                    basicPlanterBlockEntity.setChanged();
                    return ItemInteractionResult.SUCCESS;
                }
            }
            else if (PlantablesConfig.isValidSoil(heldItemId)) {
                if (level.isClientSide()) {
                    return ItemInteractionResult.SUCCESS;
                }

                if (basicPlanterBlockEntity.inventory.getStackInSlot(1).isEmpty()) {
                    ItemStack plantStack = basicPlanterBlockEntity.inventory.getStackInSlot(0);
                    if (!plantStack.isEmpty()) {
                        String plantId = RegistryHelper.getItemId(plantStack);
                        boolean validCombination = false;

                        if (PlantablesConfig.isValidSeed(plantId)) {
                            validCombination = PlantablesConfig.isSoilValidForSeed(heldItemId, plantId);
                        } else if (PlantablesConfig.isValidSapling(plantId)) {
                            validCombination = PlantablesConfig.isSoilValidForSapling(heldItemId, plantId);
                        }

                        if (!validCombination) {
                            player.displayClientMessage(Component.translatable("message.agritechevolved.invalid_plant_soil_combination"), true);
                            return ItemInteractionResult.SUCCESS;
                        }
                    }

                    ItemStack soilStack = heldItem.copyWithCount(1);
                    basicPlanterBlockEntity.inventory.setStackInSlot(1, soilStack);
                    heldItem.shrink(1);
                    level.playSound(null, pos, SoundEvents.GRAVEL_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);

                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                    basicPlanterBlockEntity.setChanged();
                    return ItemInteractionResult.SUCCESS;
                }
            }
            else if (heldItem.getItem() instanceof BlockItem) {
                ItemStack plantStack = basicPlanterBlockEntity.inventory.getStackInSlot(0);
                if (!plantStack.isEmpty() && basicPlanterBlockEntity.inventory.getStackInSlot(1).isEmpty()) {
                    String plantId = RegistryHelper.getItemId(plantStack);
                    if (PlantablesConfig.isValidSoil(heldItemId)) {
                        boolean validCombination = false;

                        if (PlantablesConfig.isValidSeed(plantId)) {
                            validCombination = PlantablesConfig.isSoilValidForSeed(heldItemId, plantId);
                        } else if (PlantablesConfig.isValidSapling(plantId)) {
                            validCombination = PlantablesConfig.isSoilValidForSapling(heldItemId, plantId);
                        }

                        if (!validCombination) {
                            if (!level.isClientSide()) {
                                player.displayClientMessage(Component.translatable("message.agritechevolved.invalid_plant_soil_combination"), true);
                            }
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                }
            }
            else if (heldItem.getItem() instanceof HoeItem) {
                ItemStack soilStack = basicPlanterBlockEntity.inventory.getStackInSlot(1);

                if (!soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem blockItem) {
                    Block soilBlock = blockItem.getBlock();
                    String soilId = RegistryHelper.getBlockId(soilBlock);

                    Map<String, String> tillableBlocks = new HashMap<>();

                    tillableBlocks.put("minecraft:dirt", "minecraft:farmland");
                    tillableBlocks.put("minecraft:grass_block", "minecraft:farmland");
                    tillableBlocks.put("minecraft:mycelium", "minecraft:farmland");
                    tillableBlocks.put("minecraft:podzol", "minecraft:farmland");
                    tillableBlocks.put("minecraft:coarse_dirt", "minecraft:farmland");
                    tillableBlocks.put("minecraft:rooted_dirt", "minecraft:farmland");

                    if (ModList.get().isLoaded("farmersdelight")) {
                        tillableBlocks.put("farmersdelight:rich_soil", "farmersdelight:rich_soil_farmland");
                    }

                    if (tillableBlocks.containsKey(soilId)) {
                        Block resultBlock = RegistryHelper.getBlock(tillableBlocks.get(soilId));
                        if (resultBlock != null) {
                            ItemStack farmlandStack = new ItemStack(resultBlock);
                            basicPlanterBlockEntity.inventory.setStackInSlot(1, farmlandStack);

                            level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

                            if (!player.getAbilities().instabuild) {
                                heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                            }

                            return ItemInteractionResult.sidedSuccess(level.isClientSide());
                        }
                    }
                }
            }
            else {
                Map<String, String> essenceToFarmland = new HashMap<>();
                essenceToFarmland.put("mysticalagriculture:inferium_essence", "mysticalagriculture:inferium_farmland");
                essenceToFarmland.put("mysticalagriculture:prudentium_essence", "mysticalagriculture:prudentium_farmland");
                essenceToFarmland.put("mysticalagriculture:tertium_essence", "mysticalagriculture:tertium_farmland");
                essenceToFarmland.put("mysticalagriculture:imperium_essence", "mysticalagriculture:imperium_farmland");
                essenceToFarmland.put("mysticalagriculture:supremium_essence", "mysticalagriculture:supremium_farmland");

                if (ModList.get().isLoaded("mysticalagradditions")) {
                    essenceToFarmland.put("mysticalagradditions:insanium_essence", "mysticalagradditions:insanium_farmland");
                }

                if (essenceToFarmland.containsKey(heldItemId)) {
                    ItemStack soilStack = basicPlanterBlockEntity.inventory.getStackInSlot(1);

                    if (!soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem blockItem) {
                        Block soilBlock = blockItem.getBlock();
                        String soilId = RegistryHelper.getBlockId(soilBlock);

                        if (soilId.equals("minecraft:farmland") ||
                                soilId.startsWith("mysticalagriculture:") && soilId.endsWith("_farmland") ||
                                soilId.startsWith("mysticalagradditions:") && soilId.endsWith("_farmland")) {

                            String farmlandId = essenceToFarmland.get(heldItemId);
                            Block resultBlock = RegistryHelper.getBlock(farmlandId);

                            if (resultBlock != null) {
                                if (soilId.equals(farmlandId)) {
                                    if (!level.isClientSide()) {
                                        player.displayClientMessage(Component.translatable("message.agritechevolved.same_farmland"), true);
                                    }
                                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                                }

                                ItemStack maFarmlandStack = new ItemStack(resultBlock);
                                basicPlanterBlockEntity.inventory.setStackInSlot(1, maFarmlandStack);

                                if (!player.getAbilities().instabuild) {
                                    stack.shrink(1);
                                }

                                level.playSound(player, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);

                                return ItemInteractionResult.sidedSuccess(level.isClientSide());
                            }
                        }
                    }
                }
            }

            if (!level.isClientSide()) {
                MenuProvider menuProvider = new SimpleMenuProvider(
                        (containerId, playerInventory, playerEntity) ->
                                new BasicPlanterMenu(containerId, playerInventory, basicPlanterBlockEntity),
                        Component.translatable("gui.agritechevolved.basic_planter")
                );

                player.openMenu(menuProvider, pos);
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.FAIL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ATEBlockEntities.BASIC_PLANTER_BE.get() ?
                (lvl, pos, blockState, blockEntity) -> BasicPlanterBlockEntity.tick(lvl, pos, blockState, (BasicPlanterBlockEntity)blockEntity) :
                null;
    }
}