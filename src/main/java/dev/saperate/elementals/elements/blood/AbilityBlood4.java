package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

import static dev.saperate.elementals.effects.BurnoutStatusEffect.BURNOUT_EFFECT;
import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;
import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityBlood4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        if (deltaT < 1500) {
            return;
        }
        PlayerEntity player = bender.player;
        boolean isNight = BloodElement.isNight(player.getWorld());

        List<LivingEntity> entities = SapsUtils.getEntitiesInRadius(player.getEyePos(), isNight ? 7 : 3, player.getWorld(), player);
        for (LivingEntity living : entities){
            if(living instanceof PlayerEntity && !isNight){
                living.addStatusEffect(new StatusEffectInstance(STUNNED_EFFECT,100,0,false,false,true));
                continue;
            }
            living.addStatusEffect(new StatusEffectInstance(STATIONARY_EFFECT,120,2,true,false,true));
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
