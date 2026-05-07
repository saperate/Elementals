package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityWater2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("waterArc")) {
            bender.setCurrAbility(null);
            return;
        }

        if (deltaT >= 1000 && playerData.canUseUpgrade("waterJet")) {
            WaterElement.get().getAbility(7).onCall(bender, deltaT);
            return;
        }
        if (bender.player.isCrouching()) {
            if (playerData.canUseUpgrade("waterBlade")) {
                WaterElement.get().getAbility(9).onCall(bender, deltaT);
                return;
            } else if (playerData.canUseUpgrade("waterCannon")) {
                WaterElement.get().getAbility(11).onCall(bender, deltaT);
                return;
            }
        }
        WaterElement.get().getAbility(3).onCall(bender, deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
