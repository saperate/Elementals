package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityEarth2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);


        if (bender.player.isSneaking()) {
            if(deltaT >= 1000){
                EarthElement.get().abilityList.get(6).onCall(bender,deltaT);
                return;
            }

            if (playerData.canUseUpgrade("mine")) {
                EarthElement.get().abilityList.get(2).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("chunkPickup")) {
                EarthElement.get().abilityList.get(3).onCall(bender,deltaT);
                return;
            }
        }

        if(playerData.canUseUpgrade("mine")){
            EarthElement.get().abilityList.get(5).onCall(bender,deltaT);
        }
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
