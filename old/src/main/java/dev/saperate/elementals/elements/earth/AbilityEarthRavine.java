package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import dev.saperate.elementals.misc.BlockRestoreManager;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.elements.earth.EarthElement.makeHole;

public class AbilityEarthRavine implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
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
        makeHole(bPos, 4, bender,damagedEntities);
        for (int i = 1; i <= range; i++) {
            makeHole(bPos.add(dx * i, 0 , dz * i), 4, bender, damagedEntities);
            
            //Fixes diagonals
            makeHole(bPos.add(dx * (i - 1), 0 , dz * i), 4, bender, damagedEntities);
            makeHole(bPos.add(dx * i, 0 , dz * (i - 1)), 4, bender, damagedEntities);

            int spread = Math.min(i,plrData.canUseUpgrade("earthRavineSpreadI") ? 4 : 2);
            for (int j = -spread; j < spread; j++) {
                if(rnd.nextBetween(0,5) == 5 || j == 0){
                    continue;
                }
                makeHole(bPos.add(dz * j + (i * dx), 0,  - (dx * j - (i * dz))), 4, bender, damagedEntities);
            }
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
