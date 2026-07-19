package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityFire1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        FireElement.get().getAbility(1).onCall(bender,deltaT);
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {

    }

}