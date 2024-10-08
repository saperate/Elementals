package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;


public class AbilityEarthSpikes implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(20)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        PlayerEntity player = bender.player;
        Random rnd = player.getRandom();
        PlayerData plrData = PlayerData.get(player);
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, false);
        BlockPos bPos = hit.getBlockPos();

        ArrayList<LivingEntity> damagedEntities = new ArrayList<>();

        int dx = (int) Math.round(-Math.sin(Math.toRadians(player.getYaw())));
        int dz = (int) Math.round(Math.cos(Math.toRadians(player.getYaw())));
        int a = 0;

        int range = plrData.canUseUpgrade("earthSpikesRangeI") ? 8 : 5;
        placeSpike(bPos, bender,damagedEntities);
        for (int i = 1; i <= range; i++) {
            placeSpike(bPos.add(dx * i, 0 , dz * i), bender, damagedEntities);


            int spread = Math.min(i,plrData.canUseUpgrade("earthSpikesSpreadI") ? 6 : 3);
            for (int j = -spread; j < spread; j++) {
                if(rnd.nextBetween(0,3) != 1 || j == 0){
                    a++;
                    continue;
                }
                placeSpike(bPos.add(dz * j + (i * dx), 0,  - (dx * j - (i * dz))), bender, damagedEntities);
            }
        }
        bender.setCurrAbility(null);
    }

    public void placeSpike(BlockPos pos, Bender bender, ArrayList<LivingEntity> damagedEntities){
        EarthElement.damageEntityAboveBlock(bender.player,pos,damagedEntities,3.5f);

        EarthBlockEntity block = new EarthBlockEntity(
                bender.player.getWorld(), bender.player,
                pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f
        );

        block.setModelShapeId(2);
        block.setBlockState(bender.player.getWorld().getBlockState(pos));
        block.setDrops(false);
        block.maxLifeTime = 60;
        block.setMovementSpeed(0.6f);
        block.setDamageOnTouch(true);
        block.setShiftToFreeze(false);
        block.setTargetPosition(pos.toCenterPos().toVector3f().add(0,1,0));

        bender.player.getWorld().spawnEntity(block);
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
