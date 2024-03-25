package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AbilityWaterCube extends Ability {

    @Override
    public void onCall(PlayerEntity player) {
        HitResult hit = player.raycast(5, 0, true);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            Block block = player.getEntityWorld().getBlockState(blockHit.getBlockPos()).getBlock();

            if(block.equals(Blocks.WATER)){
                WaterCubeEntity cube = new WaterCubeEntity(player.getWorld(),player);
                player.getWorld().spawnEntity(cube);
            }
        }
    }
}
