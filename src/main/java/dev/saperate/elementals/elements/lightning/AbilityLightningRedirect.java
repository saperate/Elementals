package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;


public class AbilityLightningRedirect implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
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


        if(!SapsUtils.safeHasStatusEffect(ElementalsStatusEffects.SHOCKED,player)){
            return;
        }

        Vec3d pos = SapsUtils.raycastFull(bender.player, 20,true).getPos();
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,player.getWorld());
        lightning.setPos(pos.x,pos.y,pos.z);
        player.getWorld().spawnEntity(lightning);

        player.heal((float) player.getStatusEffect(ElementalsStatusEffects.SHOCKED).getAmplifier() / 10);
        player.removeStatusEffect(ElementalsStatusEffects.SHOCKED);
    }


    @Override
    public void onRemove(Bender bender) {

    }
}
