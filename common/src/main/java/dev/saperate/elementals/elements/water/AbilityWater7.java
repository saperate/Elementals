package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityWater7 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("waterHealing")) {
            bender.setCurrAbility(null);
            return;
        }
        WaterElement.get().getAbility(15).onCall(bender, deltaT);
    }

    @Override
    public void onRemove(Bender bender) {
    }

}
