package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.lightning.LightningElement;


public class AbilityBlood1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if(bender.player.isSneaking() && deltaT >= 1500){
            BloodElement.get().abilityList.get(2).onCall(bender,deltaT);
            return;
        } else if (bender.player.isSneaking()) {
            BloodElement.get().abilityList.get(3).onCall(bender,deltaT);
            return;
        }

        BloodElement.get().abilityList.get(1).onCall(bender,deltaT);
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
