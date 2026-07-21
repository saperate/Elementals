package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;


public class AbilityEarthSpikes implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData plrData = PlayerData.get(bender.player);
        if (!plrData.canUseUpgrade("earthSpikes")) {
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
        BlockHitResult hit = (BlockHitResult) player.pick(5, 0, false);
        BlockPos bPos = hit.getBlockPos();

        ArrayList<LivingEntity> damagedEntities = new ArrayList<>();

        int dx = (int) Math.round(-Math.sin(Math.toRadians(player.getYRot())));
        int dz = (int) Math.round(Math.cos(Math.toRadians(player.getYRot())));
        int a = 0;

        int range = plrData.canUseUpgrade("earthSpikesRangeI") ? 8 : 4;
        placeSpike(bPos, bender,damagedEntities);
        for (int i = 1; i <= range; i++) {
            placeSpike(bPos.offset(dx * i, 0 , dz * i), bender, damagedEntities);


            int spread = plrData.canUseUpgrade("earthSpikesSpreadI") ? 6 : 2;
            for (int j = -spread; j < spread; j++) {
                if(rnd.nextInt(0,3) != 1 || j == 0){
                    a++;
                    continue;
                }
                placeSpike(bPos.offset(dz * j + (i * dx), 0,  - (dx * j - (i * dz))), bender, damagedEntities);
            }
        }
        bender.setCurrAbility(null);
    }

    public void placeSpike(BlockPos pos, Bender bender, ArrayList<LivingEntity> damagedEntities){
        //Place it one block down if there isn't a block, otherwise don't place it at all
        if(!EarthElement.isBlockBendable(pos,bender)){
            if(EarthElement.isBlockBendable(pos.below(),bender)){
                pos = pos.below();
            }else{
                return;
            }
        }

        float damage = PlayerData.get(bender.player).canUseUpgrade("earthSpikesDamageI") ? 5f : 3.5f;
        EarthElement.damageEntityAboveBlock(bender.player, pos, damagedEntities, damage);

        EarthBlockEntity block = new EarthBlockEntity(
                bender.player.level(), bender.player,
                pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f
        );

        block.setModelShapeId(2);
        block.setBlockState(bender.player.level().getBlockState(pos));
        block.setDrops(false);
        block.maxLifeTime = 60;
        block.setMovementSpeed(0.6f);
        block.setDamageOnTouch(true);
        block.setShiftToFreeze(false);
        block.setTargetPosition(pos.getCenter().toVector3f().add(0,1,0));

        bender.player.level().addFreshEntity(block);
    }


    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}