package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.fire.FireElement;

public class AbilityWater2 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if (true){
            WaterElement.get().abilityList.get(11).onCall(bender, deltaT);
            return;
        }

        if (deltaT >= 1000 && playerData.canUseUpgrade("waterJet")) {
            WaterElement.get().abilityList.get(7).onCall(bender, deltaT);
            return;
        }

        if (bender.player.isSneaking()) {
            if (playerData.canUseUpgrade("waterBlade")) {
                WaterElement.get().abilityList.get(9).onCall(bender, deltaT);
                return;
            } else if (playerData.canUseUpgrade("waterCannon")) {
                WaterElement.get().abilityList.get(11).onCall(bender, deltaT);
                return;
            }
        }
        WaterElement.get().abilityList.get(3).onCall(bender, deltaT);
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
