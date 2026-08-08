package dev.saperate.elementals.blocks;

import dev.saperate.elementals.blocks.blockstates.PieTypeProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

///The base class representing the pie blocks
public abstract class AbstractPieBlock extends Block {
    public static final PieTypeProperty PIE_TYPE = PieTypeProperty.create();
    
    public AbstractPieBlock() {
        this(BlockBehaviour.Properties.of().strength(1f));
    }
    
    public AbstractPieBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PIE_TYPE, "plain"));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIE_TYPE);
    }
}
