package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityEarth3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);

        PlayerData playerData = PlayerData.get(bender.player);
        if(!playerData.canUseUpgrade("earthPillar")){
            return;
        }
        Object[] vars = EarthElement.canBend(bender.player, false);

        if (vars != null) {
            EarthElement.get().getAbility(6).onCall(bender, deltaT);
        } else if (bender.player.isSprinting() && playerData.canUseUpgrade("earthSurf")) {
            EarthElement.get().getAbility(14).onCall(bender, deltaT);
        } else if (playerData.canUseUpgrade("earthJump")) {
            EarthElement.get().getAbility(7).onCall(bender, deltaT);
        }


    }

    @Override
    public void onRemove(Bender bender) {

    }
}