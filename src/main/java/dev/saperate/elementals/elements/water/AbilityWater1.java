package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.fire.FireElement;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

public class AbilityWater1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("waterBubble")) {
            bender.setCurrAbility(null);
            return;
        }

        if (bender.player.isSneaking()) {
            if ((playerData.canUseUpgrade("waterShieldHelmetPath")
                    || playerData.canUseUpgrade("waterShieldSuffocatePath"))  && deltaT >= 1000) {
                WaterElement.get().getAbility(5).onCall(bender, deltaT);
                return;
            } else if (playerData.canUseUpgrade("waterHelmet") && bender.player.isSubmergedInWater()
                    && !bender.player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                WaterElement.get().getAbility(4).onCall(bender, deltaT);
                return;
            } else if (playerData.canUseUpgrade("waterSuffocate")) {
                WaterElement.get().getAbility(6).onCall(bender, deltaT);
                return;
            }
        }

        WaterElement.get().getAbility(1).onCall(bender, deltaT);
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
