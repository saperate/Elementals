package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.lightning.LightningElement;


public class AbilityBlood1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        if (!playerData.canUseUpgrade("bloodPush")) {
            bender.setCurrAbility(null);
            return;
        }
        if (bender.player.isSneaking() && deltaT >= 1500 && playerData.canUseUpgrade("bloodControl")) {
            BloodElement.get().abilityList.get(2).onCall(bender, deltaT);
            return;
        } else if (bender.player.isSneaking() && playerData.canUseUpgrade("bloodShield")) {
            BloodElement.get().abilityList.get(3).onCall(bender, deltaT);
            return;
        }

        BloodElement.get().abilityList.get(1).onCall(bender, deltaT);
    }


    @Override
    public void onRemove(Bender bender) {

    }

}
