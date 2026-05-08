package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;


public class AbilityAir2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if(!playerData.canUseUpgrade("airStream")){
            bender.setCurrAbility(null);
            return;
        }


        if (playerData.canUseUpgrade("airBall") && deltaT > 1000) {
            AirElement.get().getAbility(6).onCall(bender, deltaT);
            return;
        }
        if (bender.player.isCrouching()) {
            if (playerData.canUseUpgrade("airBullets")) {
                AirElement.get().getAbility(7).onCall(bender, deltaT);
                return;
            } else if (playerData.canUseUpgrade("airSuffocate")) {
                AirElement.get().getAbility(8).onCall(bender, deltaT);
                return;
            }
        }

        AirElement.get().getAbility(5).onCall(bender, deltaT);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {

    }

}
