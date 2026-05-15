package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.fire.FireElement;


public class AbilityAir1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if(!playerData.canUseUpgrade("airGust")){
            bender.setCurrAbility(null);
            return;
        }

        if (bender.player.isShiftKeyDown()) {
            if(playerData.canUseUpgrade("airShield")){
                AirElement.get().getAbility(2).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("airTornado")) {
                AirElement.get().getAbility(3).onCall(bender,deltaT);
                return;
            }
        }

        AirElement.get().getAbility(1).onCall(bender,deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
