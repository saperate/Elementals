package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;



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
        Player player = bender.player;
        boolean isNight = BloodElement.isNight(player.level());

        int baseRange = bender.getData().canUseUpgrade("bloodParalysisRangeI") ? 5 : 3;
        List<LivingEntity> entities = SapsUtils.getEntitiesInRadius(player.getEyePosition(), isNight ? baseRange + 5 : baseRange, player.level(), player);

        for (LivingEntity living : entities) {
            if (living instanceof Player && !isNight) {
                living.addEffect(new MobEffectInstance(ElementalsStatusEffects.STUNNED.get(), 100, 0, false, false, true));
                continue;
            }
            living.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIONARY.get(), 120, 2, true, false, true));
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
