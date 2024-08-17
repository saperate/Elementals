package dev.saperate.elementals.blocks.blockEntities;

import dev.saperate.elementals.blocks.LitAir;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static dev.saperate.elementals.Elementals.LIT_AIR_BLOCK_ENTITY;
import static dev.saperate.elementals.blocks.LitAir.LIT_AIR;

public class LitAirBlockEntity extends BlockEntity{
    private int lifetime = 0;

    public LitAirBlockEntity( BlockPos pos, BlockState state) {
        super(LIT_AIR_BLOCK_ENTITY, pos, state);
    }

    public void resetTimer(){
        lifetime = 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, LitAirBlockEntity blockEntity) {
        blockEntity.lifetime++;
        if(blockEntity.lifetime >= 10){
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
