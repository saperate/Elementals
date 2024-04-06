package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityFire2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if(true){
            FireElement.get().abilityList.get(7).onCall(bender,deltaT);
            return;
        }


        if(deltaT > 1000){
            FireElement.get().abilityList.get(6).onCall(bender,deltaT);
            return;
        }

        if (bender.player.isSneaking()) {

            if(playerData.canUseUpgrade("fireShield")){
                FireElement.get().abilityList.get(7).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("")) {//flameThrower
                return;
            }
            return;
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
