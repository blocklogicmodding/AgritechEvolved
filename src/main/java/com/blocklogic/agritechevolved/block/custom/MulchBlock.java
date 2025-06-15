package com.blocklogic.agritechevolved.block.custom;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

public class MulchBlock extends Block {

    public MulchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof HoeItem) {
            return convertToInfusedFarmland(level, pos, player, stack);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof HoeItem) {
            ItemInteractionResult result = convertToInfusedFarmland(level, pos, player, mainHandItem);
            return result.result();
        }
        return InteractionResult.PASS;
    }

    private ItemInteractionResult convertToInfusedFarmland(Level level, BlockPos pos, Player player, ItemStack hoeStack) {
        if (!level.isClientSide) {
            BlockState infusedFarmland = ATEBlocks.INFUSED_FARMLAND.get().defaultBlockState();
            level.setBlockAndUpdate(pos, infusedFarmland);

            level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                hoeStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(hoeStack));
            }

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
        if (toolAction == ItemAbilities.HOE_TILL) {
            return ATEBlocks.INFUSED_FARMLAND.get().defaultBlockState();
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}