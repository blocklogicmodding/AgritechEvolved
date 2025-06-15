package com.blocklogic.agritechevolved.block.custom;

import com.blocklogic.agritechevolved.block.ATEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

import javax.annotation.Nullable;

public class InfusedFarmlandBlock extends FarmBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;

    public InfusedFarmlandBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MOISTURE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos()) ?
                Blocks.DIRT.defaultBlockState() :
                super.getStateForPlacement(context);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos.above());
        if (blockstate.is(Blocks.BAMBOO) || blockstate.is(Blocks.CACTUS) || blockstate.is(Blocks.BAMBOO_SAPLING))
            return true;
        return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        if (facing != Direction.UP) return TriState.FALSE;

        if (plant.getBlock() instanceof BushBlock)
            return TriState.TRUE;

        if (plant.getBlock() instanceof CropBlock)
            return TriState.TRUE;

        if (plant.getBlock() instanceof StemBlock)
            return TriState.TRUE;

        return TriState.DEFAULT;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);
        if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
            if (moisture > 0) {
                level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
            } else if (!shouldMaintainFarmland(level, pos)) {
                turnToMulch(null, state, level, pos);
            }
        } else if (moisture < 7) {
            level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
        }

        if (moisture == 7 && random.nextFloat() < 0.45f) {
            bonemealCrop(level, pos);
        }
    }

    private void bonemealCrop(ServerLevel level, BlockPos pos) {
        BlockPos cropPos = pos.above();
        BlockState crop = level.getBlockState(cropPos);
        BlockState newCrop = null;

        if (crop.getBlock() instanceof CropBlock cropblock) {
            if (!cropblock.isMaxAge(crop)) {
                newCrop = cropblock.getStateForAge(cropblock.getAge(crop) + 1);
            }
        } else if (crop.getBlock() instanceof StemBlock) {
            int age = crop.getValue(StemBlock.AGE);
            if (age < 7) {
                newCrop = crop.setValue(StemBlock.AGE, age + 1);
            }
        } else if (crop.is(Blocks.SWEET_BERRY_BUSH)) {
            int age = crop.getValue(SweetBerryBushBlock.AGE);
            if (age < 3) {
                newCrop = crop.setValue(SweetBerryBushBlock.AGE, age + 1);
            }
        }  else if (crop.getBlock() instanceof BonemealableBlock bonemealable) {
            if (bonemealable.isValidBonemealTarget(level, cropPos, crop)) {
                bonemealable.performBonemeal(level, level.random, cropPos, crop);
            }
        }

        if (newCrop != null) {
            level.setBlockAndUpdate(cropPos, newCrop);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    public static void turnToMulch(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState dirtState = pushEntitiesUp(state, ATEBlocks.MULCH.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, dirtState);
    }

    private static BlockState pushEntitiesUp(BlockState state, BlockState newState, Level level, BlockPos pos) {
        VoxelShape oldShape = state.getCollisionShape(level, pos);
        VoxelShape newShape = newState.getCollisionShape(level, pos);
        if (!oldShape.isEmpty() && newShape.isEmpty()) {
            return newState;
        } else {
            double yOffset = newShape.max(Direction.Axis.Y) - oldShape.max(Direction.Axis.Y);
            if (yOffset > 0.0) {
                level.getEntities(null, oldShape.bounds().move(pos)).forEach(entity -> {
                    entity.teleportTo(entity.getX(), entity.getY() + yOffset, entity.getZ());
                });
            }
            return newState;
        }
    }

    private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-6, 0, -6), pos.offset(6, 1, 6))) {
            if (level.getBlockState(nearbyPos).is(BlockTags.CROPS)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNearWater(LevelReader level, BlockPos pos) {
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-6, 0, -6), pos.offset(6, 1, 6))) {
            if (level.getFluidState(nearbyPos).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!state.canSurvive(level, pos)) {
            turnToMulch(null, state, level, pos);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}