package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.air.AirElement;


public class AbilityLightning1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if(!playerData.canUseUpgrade("lightningRedirection")){
            bender.setCurrAbility(null);
            return;
        }

        if (bender.player.isSneaking() && playerData.canUseUpgrade("lightningBolt") && deltaT >= 2000) {
            LightningElement.get().abilityList.get(1).onCall(bender,deltaT);
            return;
        }
        LightningElement.get().abilityList.get(2).onCall(bender,deltaT);
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

    @Override
    public boolean shouldImmobilizePlayer() {
        return true;
    }

}
