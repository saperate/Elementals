package dev.saperate.elementals.blocks;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
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

import java.util.Map;
import java.util.function.Supplier;

///The base class representing the pie blocks
public abstract class AbstractPieBlock extends Block {
    public static final VoxelShape shape = Block.box((double)1.0F, (double)0.0F, (double)1.0F, (double)15.0F, (double)3.0F, (double)15.0F);
    
    public AbstractPieBlock() {
        this(BlockBehaviour.Properties.of().noOcclusion().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
    }
    
    public AbstractPieBlock(BlockBehaviour.Properties properties) {
        super(properties);
        //registerDefaultState(defaultBlockState().setValue(PIE_TYPE, "plain"));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        //builder.add(PIE_TYPE);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }
    
    public static BlockState getWithCooked(BlockState state, boolean isCooked){
        AbstractPieBlock block = (AbstractPieBlock) state.getBlock();
        
        for(Map.Entry<Pair<String, Boolean>, Supplier<Block>> entry : ElementalsBlocks.PIE_BLOCKS.entrySet()){
            if(entry.getValue().get().equals(block)){
                return ElementalsBlocks.PIE_BLOCKS.get(new Pair<>(entry.getKey().getFirst(), isCooked)).get().defaultBlockState();
            }
        }
        return state;
    }

    @Override
    public Item asItem() {
        return super.asItem();
    }
}
