package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.fire.FireElement;

public class AbilityWater1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);



        if (bender.player.isSneaking()) {
            if(playerData.canUseUpgrade("")){
                return;
            } else if (playerData.canUseUpgrade("")) {
                return;
            }
        }

        WaterElement.get().abilityList.get(1).onCall(bender,deltaT);
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
