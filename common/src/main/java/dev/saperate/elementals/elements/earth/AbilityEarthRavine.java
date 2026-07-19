package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

import static dev.saperate.elementals.elements.earth.EarthElement.makeHole;

public class AbilityEarthRavine implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("earthRavine")) { // ou "earthSpikes"
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
        Player player = bender.player;
        RandomSource rnd = player.getRandom();
        PlayerData plrData = PlayerData.get(player);
        BlockHitResult hit = (BlockHitResult) player.pick(5, 0, false);
        BlockPos bPos = hit.getBlockPos();

        ArrayList<LivingEntity> damagedEntities = new ArrayList<>();

        int dx = (int) Math.round(-Math.sin(Math.toRadians(player.getYRot())));
        int dz = (int) Math.round(Math.cos(Math.toRadians(player.getYRot())));
        
        int range = plrData.canUseUpgrade("earthRavineRangeI") ? 12 : 6;
        makeHole(bPos, 4, bender,damagedEntities);
        for (int i = 1; i <= range; i++) {
            makeHole(bPos.offset(dx * i, 0 , dz * i), 4, bender, damagedEntities);
            
            //Fixes diagonals
            makeHole(bPos.offset(dx * (i - 1), 0 , dz * i), 4, bender, damagedEntities);
            makeHole(bPos.offset(dx * i, 0 , dz * (i - 1)), 4, bender, damagedEntities);

            int spread = Math.min(i,plrData.canUseUpgrade("earthRavineSpreadI") ? 4 : 2);
            for (int j = -spread; j < spread; j++) {
                if(rnd.nextInt(0,5) == 5 || j == 0){
                    continue;
                }
                makeHole(bPos.offset(dz * j + (i * dx), 0,  - (dx * j - (i * dz))), 4, bender, damagedEntities);
            }
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
