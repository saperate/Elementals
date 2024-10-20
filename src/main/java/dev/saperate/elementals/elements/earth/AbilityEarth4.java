package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.items.ElementalItems.EARTH_ARMOR_SET;

public class AbilityEarth4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData plrData = PlayerData.get(bender.player);
        bender.setCurrAbility(null);
        if (!plrData.canUseUpgrade("earthSeismicSense")) {
            return;
        }

        if (deltaT > 1000 && plrData.canUseUpgrade("earthArmor")) {
            EarthElement.get().abilityList.get(13).onCall(bender, deltaT);
            return;
        }

        if(bender.player.getInventory().containsAny(EARTH_ARMOR_SET)){
            return;
        }

        if (bender.player.hasStatusEffect(SEISMIC_SENSE_EFFECT)) {
            bender.player.removeStatusEffect(SEISMIC_SENSE_EFFECT);
        } else {
            if (!bender.reduceChi(15)) {
                if (bender.abilityData == null) {
                    bender.setCurrAbility(null);
                } else {
                    onRemove(bender);
                }
                return;
            }
            Elementals.USED_ABILITY.trigger((ServerPlayerEntity) bender.player, "seismic_sense");
            bender.player.addStatusEffect(new StatusEffectInstance(SEISMIC_SENSE_EFFECT, 2400));
        }
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
