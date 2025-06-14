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

public class PlanterBlockEntityRenderer implements BlockEntityRenderer<AdvancedPlanterBlockEntity> {

    public PlanterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AdvancedPlanterBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        if (!blockEntity.inventory.getStackInSlot(1).isEmpty()) {
            ItemStack soilStack = blockEntity.inventory.getStackInSlot(1);

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

        if (!blockEntity.inventory.getStackInSlot(0).isEmpty() && !blockEntity.inventory.getStackInSlot(1).isEmpty()) {
            ItemStack plantStack = blockEntity.inventory.getStackInSlot(0);

            if (plantStack.getItem() instanceof BlockItem blockItem) {
                String plantId = RegistryHelper.getItemId(plantStack);
                boolean isTree = PlantablesConfig.isValidSapling(plantId);
                boolean isCrop = PlantablesConfig.isValidSeed(plantId);

                if (isTree || isCrop) {
                    Block plantBlock = blockItem.getBlock();
                    BlockState plantState = plantBlock.defaultBlockState();

                    poseStack.pushPose();

                    float growthProgress = blockEntity.getGrowthProgress();

                    if (isTree) {
                        poseStack.translate(0.5, 0.45, 0.5);

                        float scale = 0.3f + (growthProgress * 0.4f);
                        poseStack.scale(scale, scale, scale);

                        poseStack.translate(-0.5, 0, -0.5);
                    } else {
                        poseStack.translate(0.1725, 0.45, 0.1725);

                        int growthStage = blockEntity.getGrowthStage();
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

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> BlockState setAgeProperty(BlockState state, Property<T> property, int age) {
        return state.setValue(property, (T)(Integer)age);
    }
}