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

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.elements.earth.EarthElement.makeHole;

public class AbilityEarthRavine implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if(bender.player.getWorld().getGameRules().getBoolean(BENDING_GRIEFING)){
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(15)) {
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

        int range = plrData.canUseUpgrade("earthRavineRangeI") ? 12 : 6;
        makeHole(bPos, 2, bender,damagedEntities);
        for (int i = 1; i <= range; i++) {
            makeHole(bPos.add(dx * i, 0 , dz * i), 2, bender, damagedEntities);

            int spread = Math.min(i,plrData.canUseUpgrade("earthRavineSpreadI") ? 4 : 2);
            for (int j = -spread; j < spread; j++) {
                if(rnd.nextBetween(0,5) == 5 || j == 0){
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
