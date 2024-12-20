package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityBlood3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        PlayerData playerData = PlayerData.get(player);
        bender.setCurrAbility(null);

        if(!playerData.canUseUpgrade("bloodStep")){
            return;
        }

        if (playerData.canUseUpgrade("bloodOvercharge") && deltaT >= 500 && !safeHasStatusEffect(ElementalsStatusEffects.OVERCHARGED, player) && !safeHasStatusEffect(ElementalsStatusEffects.BURNOUT, player) ) {
            if (!bender.reduceChi(15)) {
                return;
            }
            player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.OVERCHARGED, playerData.canUseUpgrade("bloodOverchargeStrengthI") ? 600 : 400, 1, false, false, true));
        }else if(player.isOnGround()){
            if (!bender.reduceChi(10)) {
                return;
            }
            float power = 1.5f;

            if (playerData.canUseUpgrade("bloodStepRangeII")) {
                power = 3;
            } else if (playerData.canUseUpgrade("bloodStepRangeI")) {
                power = 2;
            }
            SapsUtils.launchEntity(player,power);
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
