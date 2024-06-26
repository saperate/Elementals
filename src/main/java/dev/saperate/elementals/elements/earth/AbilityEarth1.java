package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityEarth1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if(!playerData.canUseUpgrade("earthBlock")){
            bender.setCurrAbility(null);
            return;
        }

        if (bender.player.isSneaking()) {
            if (playerData.canUseUpgrade("earthWall")) {
                EarthElement.get().abilityList.get(2).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("earthChunk")) {
                EarthElement.get().abilityList.get(3).onCall(bender,deltaT);
                return;
            }
        }

        EarthElement.get().abilityList.get(1).onCall(bender,deltaT);
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
