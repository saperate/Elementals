package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class AbilityFireSpikes implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.reduceChi(15);
        PlayerEntity player = bender.player;
        Random rnd = player.getRandom();
        PlayerData plrData = PlayerData.get(player);
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, true);
        BlockPos bPos = hit.getBlockPos();


        placeFire(bender, bPos, hit, plrData);
        int dx = (int) Math.round(-Math.sin(Math.toRadians(player.getYaw())));
        int dz = (int) Math.round(Math.cos(Math.toRadians(player.getYaw())));

        int range = PlayerData.get(player).canUseUpgrade("fireSpikesRangeI") ? 10 : 6;

        int countMod = PlayerData.get(player).canUseUpgrade("fireSpikesCountI") ? 2 : 1;

        for (int i = 1; i <= range; i++) {

            if(rnd.nextBetween(0,6 / countMod) == 0){
                placeFire(bender, bPos.add(dx * i, 0, dz * i), hit, plrData);
            }

            for (int j = -i; j < i; j++) {
                if(rnd.nextBetween(0,4 / countMod) == 0) {
                    placeFire(bender, bPos.add(dz * j + (i * dx), 0,  - (dx * j - (i * dz))), hit, plrData);
                }
            }

        }
    }

    public void placeFire(Bender bender, BlockPos bPos, BlockHitResult hit, PlayerData plrData) {
        PlayerEntity player = bender.player;

        if (AbstractFireBlock.canPlaceAt(player.getWorld(), bPos.up(), hit.getSide())) {
            FireBlockEntity entity = new FireBlockEntity(player.getWorld(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
            entity.setFinalFireHeight(1.5f);
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
