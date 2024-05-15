package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.effect.StatusEffectInstance;

import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;

public class AbilityEarth4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if(bender.player.hasStatusEffect(SEISMIC_SENSE_EFFECT)){
            bender.player.removeStatusEffect(SEISMIC_SENSE_EFFECT);
        }else {
            bender.player.addStatusEffect(new StatusEffectInstance(SEISMIC_SENSE_EFFECT,2400));
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
