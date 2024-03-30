package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class AbilityFireWall implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, true);
        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());
        BlockPos bPos = hit.getBlockPos();


        placeFire(bender,bPos,hit);

        double yawRadians = Math.toRadians(player.getYaw());
        double dx = Math.sin(-yawRadians);
        double dz = Math.cos(yawRadians);

        placeFire(bender,bPos.add((int) Math.round(dx),0,(int) Math.round(dz)),hit);
    }

    public void placeFire(Bender bender, BlockPos bPos, BlockHitResult hit){
        PlayerEntity player = bender.player;

        if( AbstractFireBlock.canPlaceAt(player.getWorld(),bPos.up(),hit.getSide())) {
            FireBlockEntity entity = new FireBlockEntity(player.getWorld(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
            player.getWorld().spawnEntity(entity);
        }
    }

    @Override
    public void onLeftClick(Bender bender) {

    }

    @Override
    public void onMiddleClick(Bender bender) {

    }

    @Override
    public void onRightClick(Bender bender) {

    }

    @Override
    public void onTick(Bender bender) {

    }
}
