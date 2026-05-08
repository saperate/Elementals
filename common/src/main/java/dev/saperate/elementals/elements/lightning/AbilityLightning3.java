package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityLightning3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
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
            player.addEffect(new MobEffectInstance(ElementalsStatusEffects.OVERCHARGED, duration, 0, false, false, true));
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
