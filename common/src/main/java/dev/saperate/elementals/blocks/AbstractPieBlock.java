package dev.saperate.elementals.blocks;

import dev.saperate.elementals.blocks.blockstates.PieTypeProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

///The base class representing the pie blocks
public abstract class AbstractPieBlock extends Block {
    public static final VoxelShape shape = Block.box((double)1.0F, (double)0.0F, (double)1.0F, (double)15.0F, (double)3.0F, (double)15.0F);
    public static final PieTypeProperty PIE_TYPE = PieTypeProperty.create();
    
    public AbstractPieBlock() {
        this(BlockBehaviour.Properties.of().noOcclusion().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
    }
    
    public AbstractPieBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PIE_TYPE, "plain"));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIE_TYPE);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }
}
