package dev.saperate.elementals.blocks;

import com.mojang.serialization.MapCodec;
import dev.saperate.elementals.blocks.blockEntities.KettleBlockEntity;
import dev.saperate.elementals.items.KettleBlockItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KettleBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(3.0F, 0.0F, 3.0F, 13.0F, 8.0F, 13.0F);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public KettleBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * Places ingredients into the kettle
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof KettleBlockEntity blockEntity)) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }

        if (player.isCrouching()) { // We are looking to remove the last ingredient added
            int lastUsedSlot = blockEntity.getLastUsedSlot();
            if (lastUsedSlot != -1) {
                SapsUtils.addOrDropItem(player, level, blockEntity.getSlot(lastUsedSlot));
                blockEntity.emptySlot(lastUsedSlot);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof KettleBlockEntity blockEntity)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        if (blockEntity.getItemFluidValue(stack) != 0) {
            if (blockEntity.putOrAddFluid(stack) && (!blockEntity.hasFluid() || blockEntity.hasWater())) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                    SapsUtils.addOrDropItem(player, level, blockEntity.removeFluidFromStack(stack));
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else if(!stack.isEmpty()) { // The item doesn't add any water to our kettle
            int firstUsableSlot = blockEntity.getFirstUsableSlot(stack);
            if (firstUsableSlot != -1) {
                ItemStack slotStack = blockEntity.getSlot(firstUsableSlot);

                if (slotStack != null && slotStack.is(stack.getItem())) { // We have to merge the stacks
                    int totalCount = slotStack.getCount() + stack.getCount();
                    stack.setCount(Math.min(stack.getMaxStackSize(), totalCount));
                    blockEntity.putInSlot(firstUsableSlot, stack);
                    
                    if(totalCount > stack.getMaxStackSize()){
                        stack.setCount(totalCount - stack.getMaxStackSize());
                    }else{
                        stack.setCount(0);
                    }
                    
                }else{ //The slot is empty, we just set it :)
                    blockEntity.putInSlot(firstUsableSlot, stack.copyAndClear());
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(KettleBlock::new);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KettleBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level.getBlockEntity(pos) instanceof KettleBlockEntity blockEntity) || state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }
        level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), KettleBlockItem.fromBlockEntity(blockEntity)));
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
