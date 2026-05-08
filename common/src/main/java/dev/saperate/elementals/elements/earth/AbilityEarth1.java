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

        if (bender.player.isCrouching()) {
            if (playerData.canUseUpgrade("earthWall")) {
                EarthElement.get().getAbility(2).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("earthChunk")) {
                EarthElement.get().getAbility(3).onCall(bender,deltaT);
                return;
            }
        }

        EarthElement.get().getAbility(1).onCall(bender,deltaT);
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
