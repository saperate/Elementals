package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;

public class AbilityFire1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);



        if (bender.player.isSneaking()) {
            if(playerData.canUseUpgrade("fireWall")){
                FireElement.get().abilityList.get(2).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("fireSpikes")) {
                FireElement.get().abilityList.get(3).onCall(bender,deltaT);
                return;
            }
        }
        FireElement.get().abilityList.get(1).onCall(bender,deltaT);
        bender.setCurrAbility(null);
    }

    @Override
    public void onLeftClick(Bender bender) {

    }

    @Override
    public void onMiddleClick(Bender bender) {

    }

    @Override
    public void onRightClick(Bender bender) {

    }

    @Override
    public void onTick(Bender bender) {

    }

}
