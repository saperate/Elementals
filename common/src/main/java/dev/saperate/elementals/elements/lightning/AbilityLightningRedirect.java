package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AbilityLightningRedirect implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        bender.setCurrAbility(null);

        float cost = 25;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("lightningRedirectionEfficiencyII")) {
            cost = 10;
        } else if (plrData.canUseUpgrade("lightningRedirectionEfficiencyI")) {
            cost = 15;
        }
        if (!bender.reduceChi(cost)) {
            return;
        }


        if(!SapsUtils.safeHasStatusEffect(ElementalsStatusEffects.SHOCKED.get(),player)){
            return;
        }

        Vec3 pos = SapsUtils.raycastFull(bender.player, 20,true).getLocation();
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT,player.level());
        lightning.setPos(pos.x,pos.y,pos.z);
        player.level().addFreshEntity(lightning);

        player.heal((float) player.getEffect(ElementalsStatusEffects.SHOCKED.get()).getAmplifier() / 10);
        player.removeEffect(ElementalsStatusEffects.SHOCKED.get());
    }


    @Override
    public void onRemove(Bender bender) {

    }
}
