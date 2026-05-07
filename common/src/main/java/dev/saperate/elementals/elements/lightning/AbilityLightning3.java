package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityLightning3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        PlayerData playerData = PlayerData.get(player);

        if(!playerData.canUseUpgrade("lightningOvercharge")){
            bender.setCurrAbility(null);
            return;
        }

        int duration = 400;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("lightningOverchargeStrengthII")) {
            duration = 800;
        } else if (plrData.canUseUpgrade("lightningOverchargeStrengthI")) {
            duration = 600;
        }
        if (!safeHasStatusEffect(ElementalsStatusEffects.OVERCHARGED, player) && !safeHasStatusEffect(ElementalsStatusEffects.BURNOUT, player) ) {
            if (!bender.reduceChi(15)) {
                return;
            }
            player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.OVERCHARGED, duration, 0, false, false, true));
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

    }

}
