package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;


import static dev.saperate.elementals.items.ElementalsItems.EARTH_ARMOR_SET;

public class AbilityEarth4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData plrData = PlayerData.get(bender.player);
        bender.setCurrAbility(null);
        if (!plrData.canUseUpgrade("earthSeismicSense")) {
            return;
        }

        if (deltaT > 1000 && plrData.canUseUpgrade("earthArmor")) {
            EarthElement.get().getAbility(13).onCall(bender, deltaT);
            return;
        }

        if(bender.player.getInventory().hasAnyOf(EARTH_ARMOR_SET)){
            return;
        }

        if (bender.player.hasEffect(ElementalsStatusEffects.SEISMIC_SENSE)) {
            bender.player.removeEffect(ElementalsStatusEffects.SEISMIC_SENSE);
        } else {
            if (!bender.reduceChi(15)) {
                if (bender.abilityData == null) {
                    bender.setCurrAbility(null);
                } else {
                    onRemove(bender);
                }
                return;
            }
            Elementals.USED_ABILITY.trigger((ServerPlayer) bender.player, "seismic_sense");
            bender.player.addEffect(new MobEffectInstance(ElementalsStatusEffects.SEISMIC_SENSE, 2400));
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }
}
