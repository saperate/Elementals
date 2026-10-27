package dev.saperate.elementals.blocks;

import com.mojang.serialization.MapCodec;
import dev.saperate.elementals.blocks.blockEntities.KettleBlockEntity;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.KettleBlockItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        
        if (tryFillKettle(stack, blockEntity, player, level)) {
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
    
    public boolean tryFillKettle(ItemStack stack, KettleBlockEntity blockEntity, Player player, Level level){
        if (blockEntity.getItemFluidValue(stack) != 0) {// It has a water value, try to add some to it
            if (blockEntity.putOrAddFluid(stack) && (!blockEntity.hasFluid() || blockEntity.hasWater())) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                    SapsUtils.addOrDropItem(player, level, blockEntity.removeFluidFromStack(stack));
                }
                return true;
            }
        } else { // Check if we could fill a container instead (ex: bucket)
            if(blockEntity.getFluid().isSame(Fluids.WATER)){
                if(stack.is(Items.BUCKET)){
                    stack.shrink(1);
                    SapsUtils.addOrDropItem(player, level, Items.BUCKET.getDefaultInstance());
                    return true;
                } else if (stack.is(Items.GLASS_BOTTLE)) {
                    stack.shrink(1);
                    SapsUtils.addOrDropItem(player, level, Items.POTION.getDefaultInstance());
                    return true;
                }
            }else{
                //TODO add tea and make it fill here
            }
        }
        return false;
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

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!(level.getBlockEntity(pos) instanceof KettleBlockEntity blockEntity) || !stack.is(ElementalsItems.KETTLE_ITEM.get())) {
            return;
        }
        KettleBlockItem item = ElementalsItems.KETTLE_ITEM.get();
        
        blockEntity.setFluid(item.getFluid(stack, level));
        blockEntity.setFluidLevel(item.getFluidLevel(stack));

        List<ItemStack> ingredients = item.getIngredients(stack);
        for (int i = 0; i <= blockEntity.getMaxSlot(); i++) {
            blockEntity.putInSlot(i, ingredients.get(i));
        }
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

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack itemstack = super.getCloneItemStack(level, pos, state);
        level.getBlockEntity(pos, ElementalsBlocks.KETTLE_BLOCK_ENTITY.get())
                .ifPresent(blockEntity -> blockEntity.saveToItem(itemstack, level.registryAccess()));
        return itemstack;
    }
}
