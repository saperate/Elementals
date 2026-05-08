package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;


public class AbilityBlood2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("bloodShot")) {
            bender.setCurrAbility(null);
            return;
        }

        BloodElement.get().getAbility(5).onCall(bender, deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
