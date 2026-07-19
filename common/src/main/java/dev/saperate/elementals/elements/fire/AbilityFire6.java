package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityFire6 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (PlayerData.get(bender.player).canUseUpgrade("fireWisp")) {
            FireElement.get().getAbility(11).onCall(bender, deltaT);
            return;
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}
