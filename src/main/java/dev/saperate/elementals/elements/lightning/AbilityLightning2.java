package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.effects.StaticAuraStatusEffect.STATIC_AURA_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityLightning2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        PlayerData playerData = PlayerData.get(player);
/*
        if(!playerData.canUseUpgrade("lightningRedirection")){
            bender.setCurrAbility(null);
            return;
        }
*/
        if (deltaT >= 1000) {
            LightningElement.get().abilityList.get(5).onCall(bender, deltaT);
            return;
        }
        if (player.isSneaking()) {
            if (safeHasStatusEffect(STATIC_AURA_EFFECT, player)) {
                player.removeStatusEffect(STATIC_AURA_EFFECT);
            } else {
                player.addStatusEffect(new StatusEffectInstance(STATIC_AURA_EFFECT, 400, 0, false, false, true));
            }
            bender.setCurrAbility(null);
            return;
        }

        LightningElement.get().abilityList.get(4).onCall(bender, deltaT);
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
