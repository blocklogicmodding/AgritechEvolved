package com.blocklogic.agritechevolved.block.entity.renderer;

import com.blocklogic.agritechevolved.block.entity.AdvancedPlanterBlockEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class AdvancedPlanterBlockEntityRenderer implements BlockEntityRenderer<AdvancedPlanterBlockEntity> {

    public AdvancedPlanterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(AdvancedPlanterBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        renderSoilBlock(blockEntity, poseStack, bufferSource, packedLight);

        renderPlant(blockEntity, poseStack, bufferSource, packedLight);
    }

    private void renderSoilBlock(AdvancedPlanterBlockEntity blockEntity, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight) {
        ItemStack soilStack = blockEntity.inventory.getStackInSlot(1);

        if (soilStack.isEmpty() || !(soilStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockState soilState = blockItem.getBlock().defaultBlockState();

        poseStack.pushPose();

        poseStack.translate(0.175, 0.4, 0.175);
        poseStack.scale(0.65f, 0.05f, 0.65f);

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.renderSingleBlock(soilState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    private void renderPlant(AdvancedPlanterBlockEntity blockEntity, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight) {
        ItemStack plantStack = blockEntity.inventory.getStackInSlot(0);
        ItemStack soilStack = blockEntity.inventory.getStackInSlot(1);

        if (plantStack.isEmpty() || soilStack.isEmpty()) {
            return;
        }

        int growthStage = blockEntity.getGrowthStage();

        if (growthStage <= 0) {
            return;
        }

        String plantId = RegistryHelper.getItemId(plantStack);
        boolean isTree = PlantablesConfig.isValidSapling(plantId);
        boolean isCrop = PlantablesConfig.isValidSeed(plantId);

        if (!isTree && !isCrop) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(0.1725, 0.45, 0.1725);

        if (isTree) {
            renderTreePlant(plantStack, growthStage, poseStack, bufferSource, packedLight);
        } else {
            renderCropPlant(plantStack, growthStage, poseStack, bufferSource, packedLight);
        }

        poseStack.popPose();
    }

    private void renderCropPlant(ItemStack plantStack, int growthStage, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight) {
        float growthScale = 0.2f + (growthStage / 8.0f) * 0.5f;
        poseStack.scale(0.65f, growthScale, 0.65f);

        BlockState cropState = getCropBlockState(plantStack, growthStage);

        if (cropState != null) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            dispatcher.renderSingleBlock(cropState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    private void renderTreePlant(ItemStack plantStack, int growthStage, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight) {
        float growthScale = growthStage > 0 ? 0.7f : 0.2f;
        poseStack.scale(0.65f, growthScale, 0.65f);

        if (plantStack.getItem() instanceof BlockItem blockItem) {
            Block saplingBlock = blockItem.getBlock();
            BlockState saplingState = saplingBlock.defaultBlockState();

            BlockState finalState = getSaplingBlockState(saplingState, growthStage);

            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            dispatcher.renderSingleBlock(finalState, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    private BlockState getCropBlockState(ItemStack stack, int age) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        Block cropBlock = blockItem.getBlock();
        BlockState state = cropBlock.defaultBlockState();

        if (state.hasProperty(BlockStateProperties.AGE_1)) {
            return state.setValue(BlockStateProperties.AGE_1, Math.min(age, 1));
        } else if (state.hasProperty(BlockStateProperties.AGE_2)) {
            return state.setValue(BlockStateProperties.AGE_2, Math.min(age, 2));
        } else if (state.hasProperty(BlockStateProperties.AGE_3)) {
            return state.setValue(BlockStateProperties.AGE_3, Math.min(age, 3));
        } else if (state.hasProperty(BlockStateProperties.AGE_5)) {
            return state.setValue(BlockStateProperties.AGE_5, Math.min(age, 5));
        } else if (state.hasProperty(BlockStateProperties.AGE_7)) {
            return state.setValue(BlockStateProperties.AGE_7, Math.min(age, 7));
        }

        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals("age") && property instanceof IntegerProperty intProp) {
                int maxAge = intProp.getPossibleValues().stream()
                        .max(Integer::compareTo)
                        .orElse(0);
                int clampedAge = Math.min(age, maxAge);
                return setAgeProperty(state, intProp, clampedAge);
            }
        }

        return state;
    }

    private BlockState getSaplingBlockState(BlockState saplingState, int growthStage) {
        for (Property<?> property : saplingState.getProperties()) {
            if (property.getName().equals("stage") && property instanceof IntegerProperty intProp) {
                int maxStage = intProp.getPossibleValues().stream()
                        .max(Integer::compareTo)
                        .orElse(0);
                int clampedStage = Math.min(growthStage, maxStage);
                return setAgeProperty(saplingState, intProp, clampedStage);
            }
        }

        return saplingState;
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> BlockState setAgeProperty(BlockState state, Property<T> property, int age) {
        return state.setValue(property, (T)(Integer)age);
    }
}