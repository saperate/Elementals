package dev.saperate.elementals.blocks;

import com.mojang.serialization.MapCodec;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.BlockView;

import static dev.saperate.elementals.blocks.ElementalsBlocks.LIT_AIR_BLOCK_ENTITY;

public class LitAir extends BaseEntityBlock {

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    public RenderShape getRenderType(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public LitAir(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    public static Block registerBlock() {
        return ElementalsBlocks.LIT_AIR;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LitAirBlockEntity(pos, state);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, LIT_AIR_BLOCK_ENTITY, LitAirBlockEntity::tick);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

}
