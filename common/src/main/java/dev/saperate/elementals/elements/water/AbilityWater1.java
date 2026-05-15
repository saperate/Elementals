package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.effect.MobEffects;

public class AbilityWater1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("waterBubble")) {
            bender.setCurrAbility(null);
            return;
        }

        if (bender.player.isShiftKeyDown()) {
            if ((playerData.canUseUpgrade("waterShieldHelmetPath")
                    || playerData.canUseUpgrade("waterShieldSuffocatePath"))  && deltaT >= 1000) {
                WaterElement.get().getAbility(5).onCall(bender, deltaT);
                return;
            } else if (playerData.canUseUpgrade("waterHelmet") && bender.player.isUnderWater()
                    && !bender.player.hasEffect(MobEffects.WATER_BREATHING)) {
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
    public void onRemove(Bender bender) {

    }

}
