package dev.saperate.elementals.blocks.blockEntities;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static dev.saperate.elementals.blocks.ElementalsBlocks.LIT_AIR_BLOCK_ENTITY;

public class LitAirBlockEntity extends BlockEntity {
    private int lifetime = 0;

    public LitAirBlockEntity(BlockPos pos, BlockState state) {
        super(LIT_AIR_BLOCK_ENTITY.get(), pos, state);
    }

    public void resetTimer(){
        lifetime = 0;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, LitAirBlockEntity blockEntity) {
        blockEntity.lifetime++;
        if(blockEntity.lifetime >= 10){
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }
}
