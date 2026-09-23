package dev.saperate.elementals.blocks;

import com.mojang.serialization.MapCodec;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.elementals.blocks.ElementalsBlocks.GRILL_BLOCK_ENTITY;
import static dev.saperate.elementals.blocks.ElementalsBlocks.LIT_AIR_BLOCK_ENTITY;

public class GrillBlock extends BaseEntityBlock {

    public GrillBlock(Properties settings) {
        super(settings);
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
                return InteractionResult.SUCCESS;
            }
        } else { // We are looking to remove the contained block
            if (blockEntity.hasBlock()) {
                ItemStack stack = blockEntity.removeBlock().getBlock().asItem().getDefaultInstance();
                SapsUtils.addOrDropItem(player, level, stack);
                return InteractionResult.SUCCESS;
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
                if (blockEntity.addCampfire(blockItem.getBlock().defaultBlockState())) {
                    stack.shrink(1);
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (blockEntity.isPlaceable(blockItem.getBlock()) && !blockEntity.hasBlock()) {
                if (blockEntity.addBlock(blockItem.getBlock().defaultBlockState())) {
                    stack.shrink(1);
                    return ItemInteractionResult.SUCCESS;
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
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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

}
