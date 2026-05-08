package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;

public class AbilityFire1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if (bender.player.isCrouching()) {
            if(playerData.canUseUpgrade("fireWall")){
                FireElement.get().getAbility(2).onCall(bender,deltaT);
                bender.setCurrAbility(null);//TODO move these in their respective ability
                return;
            } else if (playerData.canUseUpgrade("fireSpikes")) {
                FireElement.get().getAbility(3).onCall(bender,deltaT);
                bender.setCurrAbility(null);
                return;
            }
        }
        FireElement.get().getAbility(1).onCall(bender,deltaT);
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {

    }

}
