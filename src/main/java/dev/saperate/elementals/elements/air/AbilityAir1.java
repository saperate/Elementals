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

        if (bender.player.isSneaking()) {
            if(playerData.canUseUpgrade("airShield")){
                AirElement.get().abilityList.get(2).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("airTornado")) {
                AirElement.get().abilityList.get(3).onCall(bender,deltaT);
                return;
            }
        }

        AirElement.get().abilityList.get(1).onCall(bender,deltaT);
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
