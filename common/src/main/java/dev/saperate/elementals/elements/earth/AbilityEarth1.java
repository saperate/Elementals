package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityEarth1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("earthBlock")) {
            bender.setCurrAbility(null);
            return;
        }

        EarthElement.get().getAbility(1).onCall(bender, deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }
}
