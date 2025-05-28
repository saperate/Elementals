package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;

public class AbilityFire2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);


        if(deltaT >= 1000 && playerData.canUseUpgrade("fireBall")){
            FireElement.get().getAbility(6).onCall(bender,deltaT);
            return;
        }

        if (bender.player.isSneaking()) {
            if(playerData.canUseUpgrade("fireShield")){
                FireElement.get().getAbility(7).onCall(bender,deltaT);
                return;
            } else if (playerData.canUseUpgrade("flameThrower"))  {
                FireElement.get().getAbility(8).onCall(bender,deltaT);
                return;
            }
        }

        if(playerData.canUseUpgrade("fireArc")){
            FireElement.get().getAbility(5).onCall(bender,deltaT);
            return;
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
