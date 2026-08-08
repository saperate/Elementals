package dev.saperate.elementals.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class CookedPieBlock extends AbstractPieBlock {
    public CookedPieBlock() {
        this(BlockBehaviour.Properties.of().strength(1f));
    }

    public CookedPieBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
