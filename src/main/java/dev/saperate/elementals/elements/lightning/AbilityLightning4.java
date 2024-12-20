package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;



public class AbilityLightning4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        PlayerData playerData = PlayerData.get(player);
//todo mention this
        if (!playerData.canUseUpgrade("lightningStorm") || !playerData.canUseUpgrade("lightningBolt")) {
            bender.setCurrAbility(null);
            return;
        }

        if (deltaT >= 3000 && player.isSneaking()) {
            LightningElement.get().abilityList.get(8).onCall(bender, deltaT);
            return;
        }
        bender.setCurrAbility(null);
    }


    @Override
    public void onRemove(Bender bender) {

    }

}
