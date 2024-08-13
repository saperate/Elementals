package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import static dev.saperate.elementals.effects.ShockedStatusEffect.SHOCKED_EFFECT;

public class AbilityLightningRedirect implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);

        PlayerEntity player = bender.player;

        if(!SapsUtils.safeHasStatusEffect(SHOCKED_EFFECT,player)){
            return;
        }

        Vec3d pos = SapsUtils.raycastFull(bender.player, 20,true).getPos();
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,player.getWorld());
        lightning.setPos(pos.x,pos.y,pos.z);
        player.getWorld().spawnEntity(lightning);

        player.heal((float) player.getStatusEffect(SHOCKED_EFFECT).getAmplifier() / 10);
        player.removeStatusEffect(SHOCKED_EFFECT);
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
