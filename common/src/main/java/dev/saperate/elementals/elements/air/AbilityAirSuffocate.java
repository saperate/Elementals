package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterHelmetEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static dev.saperate.elementals.utils.SapsUtils.raycastFull;

public class AbilityAirSuffocate implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(5)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;

        HitResult hit = raycastFull(player,12,false);
        if(hit == null || !hit.getType().equals(HitResult.Type.ENTITY)){
            bender.setCurrAbility(null);
            return;
        }

        EntityHitResult eHit = (EntityHitResult) hit;
        if (eHit.getEntity() instanceof LivingEntity victim) {

            WaterHelmetEntity entity = new WaterHelmetEntity(player.level(), victim, player.getX(), player.getY(), player.getZ());
            entity.suffocate = true;
            entity.setCaster(player);
            entity.setModelId(1);
            player.level().addFreshEntity(entity);

            bender.abilityData = entity;
            bender.setCurrAbility(this);
            return;
        }


        bender.setCurrAbility(null);
    }


    @Override
    public void onTick(Bender bender) {
        if(bender.abilityData == null || ((WaterHelmetEntity)bender.abilityData).getOwner() == null){
            onRemove(bender);
            return;
        }
        if (!bender.reduceChi(0.2f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        double distance = ((WaterHelmetEntity)bender.abilityData)
                .getOwner().position().subtract(bender.player.position()).length();


        if (!bender.player.isShiftKeyDown()
                || distance > 15) {
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        WaterHelmetEntity entity = (WaterHelmetEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.discard();
    }

}
