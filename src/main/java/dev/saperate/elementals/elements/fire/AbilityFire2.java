package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityFire2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        System.out.println("called 2");

        if (bender.player.isSneaking()) {
            if(playerData.canUseUpgrade("")){
                return;
            } else if (playerData.canUseUpgrade("")) {
                return;
            }
        }

        FireElement.get().abilityList.get(5).onCall(bender,deltaT);
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
