package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;


public class AbilityAir3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if(!playerData.canUseUpgrade("airJump")){
            bender.setCurrAbility(null);
            return;
        }

        if (deltaT >= 500 && !bender.player.isPassenger() //0.5 seconds
                && playerData.canUseUpgrade("airScooter")) {
            AirElement.get().getAbility(10).onCall(bender, deltaT);
            return;
        }
        AirElement.get().getAbility(11).onCall(bender, deltaT);

    }

    @Override
    public void onRemove(Bender bender) {

    }

}
