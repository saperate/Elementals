package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityLightning2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        PlayerData playerData = PlayerData.get(player);

        if(!playerData.canUseUpgrade("lightningVoltArc")){
            bender.setCurrAbility(null);
            return;
        }

        if (deltaT >= 1000 && playerData.canUseUpgrade("lightningEMP")) {
            LightningElement.get().getAbility(5).onCall(bender, deltaT);
            return;
        }
        if (player.isShiftKeyDown() && playerData.canUseUpgrade("lightningStaticAura")) {
            if (safeHasStatusEffect(ElementalsStatusEffects.STATIC_AURA.get(), player)) {
                player.removeEffect(ElementalsStatusEffects.STATIC_AURA.get());
            } else {
                int duration = 200;
                PlayerData plrData = PlayerData.get(player);
                if (plrData.canUseUpgrade("lightningStaticAuraStrengthII")) {
                    duration = 600;//TODO make it have a thorns effect
                } else if (plrData.canUseUpgrade("lightningStaticAuraStrengthI")) {
                    duration = 400;
                }
                player.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIC_AURA.get(), duration, 0, false, false, true));
            }
            bender.setCurrAbility(null);
            return;
        }

        LightningElement.get().getAbility(4).onCall(bender, deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }
}
