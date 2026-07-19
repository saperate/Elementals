package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityEarth2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("earthMine")) {
            bender.setCurrAbility(null);
            return;
        }

        if (bender.player.isShiftKeyDown() && deltaT >= 500 && playerData.canUseUpgrade("earthTrap")) {
            EarthElement.get().getAbility(6).onCall(bender, deltaT);
            return;
        }

        EarthElement.get().getAbility(5).onCall(bender, deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }
}