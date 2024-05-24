package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;


public class AbilityAir2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if (true) {
            AirElement.get().abilityList.get(6).onCall(bender, deltaT);
            return;
        }
        if (bender.player.isSneaking()) {
            if (playerData.canUseUpgrade("airShield")) {

                return;
            } else if (playerData.canUseUpgrade("airTornado")) {

                return;
            }
        }

        AirElement.get().abilityList.get(5).onCall(bender, deltaT);
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
