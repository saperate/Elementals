package dev.saperate.elementals.blocks;

import com.mojang.serialization.MapCodec;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
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
        
        if(player.isCrouching()){ // We are looking to remove the campfire
            if(blockEntity.hasCampfire()){
                player.getInventory().add(blockEntity.removeCampfire().getBlock().asItem().getDefaultInstance());
                return InteractionResult.SUCCESS;
            }
        }else{ // We are looking to remove the contained block
            if(blockEntity.hasBlock()){
                player.getInventory().add(blockEntity.removeBlock().getBlock().asItem().getDefaultInstance());
                return InteractionResult.SUCCESS;
            }
        }

        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
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

}
