package com.blocklogic.agritechevolved.block.entity.renderer;

import com.blocklogic.agritechevolved.block.entity.AdvancedPlanterBlockEntity;
import com.blocklogic.agritechevolved.block.entity.BasicPlanterBlockEntity;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PlanterBlockEntityRenderer implements BlockEntityRenderer<BlockEntity> {

    public PlanterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        // Support both Advanced and Basic Planters
        ItemStackHandler inventory;
        float growthProgress;
        int growthStage;

        if (blockEntity instanceof AdvancedPlanterBlockEntity advancedPlanter) {
            inventory = advancedPlanter.inventory;
            growthProgress = advancedPlanter.getGrowthProgress();
            growthStage = advancedPlanter.getGrowthStage();
        } else if (blockEntity instanceof BasicPlanterBlockEntity basicPlanter) {
            inventory = basicPlanter.inventory;
            growthProgress = basicPlanter.getGrowthProgress();
            growthStage = basicPlanter.getGrowthStage();
        } else {
            return; // Not a planter we can render
        }

        // Render soil block if present
        if (!inventory.getStackInSlot(1).isEmpty()) {
            ItemStack soilStack = inventory.getStackInSlot(1);

            if (soilStack.getItem() instanceof BlockItem blockItem) {
                BlockState soilState = blockItem.getBlock().defaultBlockState();

                poseStack.pushPose();
                poseStack.translate(0.175, 0.4, 0.175);
                poseStack.scale(0.65f, 0.05f, 0.65f);

                BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                dispatcher.renderSingleBlock(soilState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);

                poseStack.popPose();
            }
        }

        // Render plant if both plant and soil are present
        if (!inventory.getStackInSlot(0).isEmpty() && !inventory.getStackInSlot(1).isEmpty()) {
            ItemStack plantStack = inventory.getStackInSlot(0);

            if (plantStack.getItem() instanceof BlockItem blockItem) {
                String plantId = RegistryHelper.getItemId(plantStack);
                boolean isTree = PlantablesConfig.isValidSapling(plantId);
                boolean isCrop = PlantablesConfig.isValidSeed(plantId);

                if (isTree || isCrop) {
                    Block plantBlock = blockItem.getBlock();
                    BlockState plantState = plantBlock.defaultBlockState();

                    poseStack.pushPose();

                    if (isTree) {
                        poseStack.translate(0.5, 0.45, 0.5);

                        float scale = 0.3f + (growthProgress * 0.4f);
                        poseStack.scale(scale, scale, scale);

                        poseStack.translate(-0.5, 0, -0.5);
                    } else {
                        poseStack.translate(0.1725, 0.45, 0.1725);

                        plantState = getCropBlockState(plantStack, growthStage);

                        float actualProgress = Math.min(1.0f, growthProgress);
                        float growthScale = 0.2f + (actualProgress * 0.5f);
                        poseStack.scale(0.65f, growthScale, 0.65f);
                    }

                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    dispatcher.renderSingleBlock(plantState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);

                    poseStack.popPose();
                }
            }
        }
    }

    private BlockState getCropBlockState(ItemStack stack, int age) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return null;

        Block block = blockItem.getBlock();
        BlockState defaultState = block.defaultBlockState();

        // Try to find the age property
        for (Property<?> property : defaultState.getProperties()) {
            if (property instanceof IntegerProperty intProperty) {
                if (property.getName().equals("age")) {
                    int maxAge = intProperty.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(7);
                    int clampedAge = Math.min(age, maxAge);
                    return defaultState.setValue(intProperty, clampedAge);
                }
            }
        }

        // Fallback: try standard age property
        if (defaultState.hasProperty(BlockStateProperties.AGE_7)) {
            return defaultState.setValue(BlockStateProperties.AGE_7, Math.min(age, 7));
        } else if (defaultState.hasProperty(BlockStateProperties.AGE_3)) {
            return defaultState.setValue(BlockStateProperties.AGE_3, Math.min(age, 3));
        } else if (defaultState.hasProperty(BlockStateProperties.AGE_5)) {
            return defaultState.setValue(BlockStateProperties.AGE_5, Math.min(age, 5));
        } else if (defaultState.hasProperty(BlockStateProperties.AGE_15)) {
            return defaultState.setValue(BlockStateProperties.AGE_15, Math.min(age, 15));
        } else if (defaultState.hasProperty(BlockStateProperties.AGE_25)) {
            return defaultState.setValue(BlockStateProperties.AGE_25, Math.min(age, 25));
        }

        return defaultState;
    }
}