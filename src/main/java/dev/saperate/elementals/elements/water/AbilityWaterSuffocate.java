package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterHelmetEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityWaterSuffocate implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        HitResult hit = raycastFull(player,12,false);
        if(hit == null || !hit.getType().equals(HitResult.Type.ENTITY)){
            bender.setCurrAbility(null);
            return;
        }

        EntityHitResult eHit = (EntityHitResult) hit;
        if (eHit.getEntity() instanceof LivingEntity victim) {

            WaterHelmetEntity entity = new WaterHelmetEntity(player.getWorld(), victim, player.getX(), player.getY(), player.getZ());
            entity.setDrown(true);
            entity.setCaster(player);
            player.getWorld().spawnEntity(entity);

            bender.abilityData = entity;
            bender.setCurrAbility(this);
            return;
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
        if(bender.abilityData == null || ((WaterHelmetEntity)bender.abilityData).getOwner() == null){
            onRemove(bender);
            return;
        }

        double distance = ((WaterHelmetEntity)bender.abilityData)
                .getOwner().getPos().subtract(bender.player.getPos()).length();


        bender.player.addStatusEffect(new StatusEffectInstance(STATIONARY_EFFECT, 1, 1, false, false, false));
        if (!bender.player.isSneaking()
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
