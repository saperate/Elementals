package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityFire2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);


        if(deltaT >= 1000 && playerData.canUseUpgrade("fireball")){
            FireElement.get().abilityList.get(6).onCall(bender,deltaT);
            return;
        }

        if (bender.player.isSneaking()) {
            if(playerData.canUseUpgrade("fireShield")){
                FireElement.get().abilityList.get(7).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("flameThrower")) {
                FireElement.get().abilityList.get(8).onCall(bender,deltaT);
                return;
            }
        }

        if(playerData.canUseUpgrade("fireArc")){
            FireElement.get().abilityList.get(5).onCall(bender,deltaT);
            return;
        }
        bender.setCurrAbility(null);
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
