package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;


public class AbilityAir3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if (bender.player.isSprinting() && deltaT >= 500 && !bender.player.hasVehicle()) {//0.5 seconds
            AirElement.get().abilityList.get(10).onCall(bender, deltaT);
            return;
        }
        bender.player.dismountVehicle();
        AirElement.get().abilityList.get(11).onCall(bender, deltaT);

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
