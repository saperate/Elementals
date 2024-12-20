package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
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

        float cost = 50;
        if (bender.getData().canUseUpgrade("bloodParalysisEfficiencyII")) {
            cost = 25;
        } else if (bender.getData().canUseUpgrade("bloodParalysisEfficiencyI")) {
            cost = 35;
        }
        if (deltaT < 1500 || !bender.reduceChi(cost)) {
            return;
        }
        PlayerEntity player = bender.player;
        boolean isNight = BloodElement.isNight(player.getWorld());

        int baseRange = bender.getData().canUseUpgrade("bloodParalysisRangeI") ? 5 : 3;
        List<LivingEntity> entities = SapsUtils.getEntitiesInRadius(player.getEyePos(), isNight ? baseRange + 5 : baseRange, player.getWorld(), player);

        for (LivingEntity living : entities) {
            if (living instanceof PlayerEntity && !isNight) {
                living.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.STUNNED, 100, 0, false, false, true));
                continue;
            }
            living.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.STATIONARY, 120, 2, true, false, true));
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
