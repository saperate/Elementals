package dev.saperate.elementals.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class UncookedPieBlock extends AbstractPieBlock {
    public UncookedPieBlock() {
        this(BlockBehaviour.Properties.of().strength(1f));
    }

    public UncookedPieBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
