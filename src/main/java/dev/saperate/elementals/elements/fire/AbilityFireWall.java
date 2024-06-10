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
        bender.reduceChi(15);
        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, true);
        BlockPos bPos = hit.getBlockPos();


        placeFire(bender,bPos,hit, plrData);
        double dx = -Math.sin(Math.toRadians(player.getYaw() - 90));//Side to side
        double dz = Math.cos(Math.toRadians(player.getYaw() - 90));

        for (int i = 1; i <= (plrData.canUseUpgrade("fireWallWideI") ? 8 : 4); i++) {
            int dxScaled = (int) Math.round(dx * i);
            int dzScaled = (int) Math.round(dz * i);

            placeFire(bender,bPos.add(dxScaled, 0, dzScaled), hit, plrData);
            placeFire(bender,bPos.add(-dxScaled, 0, -dzScaled), hit, plrData);
        }
    }

    public void placeFire(Bender bender, BlockPos bPos, BlockHitResult hit, PlayerData plrData){
        PlayerEntity player = bender.player;

        if( AbstractFireBlock.canPlaceAt(player.getWorld(),bPos.up(),hit.getSide())) {
            FireBlockEntity entity = new FireBlockEntity(player.getWorld(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
            entity.setFinalFireHeight(plrData.canUseUpgrade("fireWallTallI") ? 2.5f : 1.5f );
            player.getWorld().spawnEntity(entity);
        }
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {

    }
    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
