package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.entity.player.Player;


public class AbilityLightning1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if(!playerData.canUseUpgrade("lightningRedirection")){
            bender.setCurrAbility(null);
            return;
        }

        if (bender.player.isShiftKeyDown() && playerData.canUseUpgrade("lightningBolt") && deltaT >= 2000) {
            LightningElement.get().getAbility(1).onCall(bender,deltaT);
            return;
        }
        LightningElement.get().getAbility(2).onCall(bender,deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }

    @Override
    public boolean shouldImmobilizePlayer(Player player) {
        return true;
    }

}
