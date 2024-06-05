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
            EarthElement.get().abilityList.get(10).onCall(bender,deltaT);
        } else if (bender.player.isOnGround() && playerData.canUseUpgrade("earthJump")) {
            EarthElement.get().abilityList.get(11).onCall(bender,deltaT);
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
