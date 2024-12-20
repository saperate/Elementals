package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityLightning2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        PlayerData playerData = PlayerData.get(player);

        if(!playerData.canUseUpgrade("lightningVoltArc")){
            bender.setCurrAbility(null);
            return;
        }

        if (deltaT >= 1000 && playerData.canUseUpgrade("lightningEMP")) {
            LightningElement.get().abilityList.get(5).onCall(bender, deltaT);
            return;
        }
        if (player.isSneaking() && playerData.canUseUpgrade("lightningStaticAura")) {
            if (safeHasStatusEffect(ElementalsStatusEffects.STATIC_AURA, player)) {
                player.removeStatusEffect(ElementalsStatusEffects.STATIC_AURA);
            } else {
                int duration = 200;
                PlayerData plrData = PlayerData.get(player);
                if (plrData.canUseUpgrade("lightningStaticAuraStrengthII")) {
                    duration = 600;
                } else if (plrData.canUseUpgrade("lightningStaticAuraStrengthI")) {
                    duration = 400;
                }
                player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.STATIC_AURA, duration, 0, false, false, true));
            }
            bender.setCurrAbility(null);
            return;
        }

        LightningElement.get().abilityList.get(4).onCall(bender, deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }
}
