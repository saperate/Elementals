package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static dev.saperate.elementals.elements.earth.EarthElement.makeHole;
import static dev.saperate.elementals.entities.earth.EarthBlockEntity.EARTHBLOCK;

public class AbilityEarthRavine implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        Random rnd = player.getRandom();
        PlayerData plrData = PlayerData.get(player);
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, false);
        BlockPos bPos = hit.getBlockPos();

        ArrayList<LivingEntity> damagedEntities = new ArrayList<>();

        int dx = (int) Math.round(-Math.sin(Math.toRadians(player.getYaw())));
        int dz = (int) Math.round(Math.cos(Math.toRadians(player.getYaw())));

        makeHole(bPos, 2, bender,damagedEntities);
        for (int i = 1; i <= 6; i++) {
            makeHole(bPos.add(dx * i, 0 , dz * i), 2, bender, damagedEntities);

            //TODO maybe make this an upgrade so you could choose between narrower and farther and larger but closer
            int spread = Math.min(i,2);//TODO add this to a config file
            for (int j = -spread; j < spread; j++) {
                if(rnd.nextBetween(0,5) == 5 || j == 0){//TODO this could also be an upgrade
                    continue;
                }
                makeHole(bPos.add(dz * j + (i * dx), 0,  - (dx * j - (i * dz))), 2, bender, damagedEntities);
            }
        }
        bender.setCurrAbility(null);
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
