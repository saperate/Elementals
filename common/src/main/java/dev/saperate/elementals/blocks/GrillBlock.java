package dev.saperate.elementals.blocks;

import com.mojang.serialization.MapCodec;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import dev.saperate.elementals.items.IGrillableItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.elementals.blocks.ElementalsBlocks.GRILL_BLOCK_ENTITY;

public class GrillBlock extends BaseEntityBlock {
    /// The shape of the block when it is facing north or south
    private static final VoxelShape SHAPE_NS = Block.box(2.0F, 0.0F, 0.0F, 14.0F, 13.0F, 16.0F);
    /// The shape of the block when it is facing east or west
    private static final VoxelShape SHAPE_EW = Block.box(0.0F, 0.0F, 2.0F, 16.0F, 13.0F, 14.0F);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public GrillBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * Removes the block or the campfire from the grill <br>
     * If the player is crouching, it will remove the campfire. Otherwise, the block is removed
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof GrillBlockEntity blockEntity)) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }

        if (player.isCrouching()) { // We are looking to remove the campfire
            if (blockEntity.hasCampfire()) {
                    ItemStack stack = blockEntity.removeCampfire().getBlock().asItem().getDefaultInstance();
                    SapsUtils.addOrDropItem(player, level, stack);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else { // We are looking to remove the contained block
            if (blockEntity.hasContainedBlock()) {
                    ItemStack stack = blockEntity.removeContainedBlock().getBlock().asItem().getDefaultInstance();
                    SapsUtils.addOrDropItem(player, level, stack);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof GrillBlockEntity blockEntity)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof CampfireBlock && !blockEntity.hasCampfire()) {
                if (blockEntity.putCampfire(blockItem.getBlock().defaultBlockState())) {
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            } else if (blockEntity.isPlaceable(blockItem) && !blockEntity.hasContainedBlock()) {
                if (blockEntity.putContainedBlock(blockItem.getBlock().defaultBlockState())) {
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        if (stack.getItem() instanceof ShovelItem) {
                blockEntity.setCampfireLit(false);
            return ItemInteractionResult.SUCCESS;
        } else if (stack.getItem() instanceof FlintAndSteelItem) {
                blockEntity.setCampfireLit(true);
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(GrillBlock::new);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(FACING).getAxis() == Direction.Axis.X) {
            return SHAPE_NS;
        } else {
            return SHAPE_EW;
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public static Direction getDirection(BlockState state) {
        return state.getValue(FACING);
    }

    public static Direction getDirection(GrillBlockEntity blockEntity) {
        return blockEntity.getBlockState().getValue(FACING);
    }

    public static int getLightLevel(BlockState state) {
        return 15;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GrillBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, GRILL_BLOCK_ENTITY.get(), GrillBlockEntity::tick);
    }

    ///Mostly taken from mojang
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof GrillBlockEntity blockEntity)) {
            return;
        }

        if (blockEntity.hasCampfire() && blockEntity.isCampfireLit()) {
            if (random.nextInt(10) == 0) {
                level.playLocalSound(
                        (double) pos.getX() + 0.5,
                        (double) pos.getY() + 0.5,
                        (double) pos.getZ() + 0.5,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F,
                        false
                );
            }

            if (random.nextInt(5) == 0) {
                for (int i = 0; i < random.nextInt(1) + 1; i++) {
                    level.addParticle(
                            ParticleTypes.LAVA,
                            (double) pos.getX() + 0.5,
                            (double) pos.getY() + 0.5,
                            (double) pos.getZ() + 0.5,
                            (double) (random.nextFloat() / 2.0F),
                            5.0E-5,
                            (double) (random.nextFloat() / 2.0F)
                    );
                }
            }
        }
    }
}
