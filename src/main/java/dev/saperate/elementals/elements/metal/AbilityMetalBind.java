package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalCableEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AbilityMetalBind implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);

        if(!bender.isAbilityInBackground(this)){
            PlayerEntity player = bender.player;
            HitResult hitResult = SapsUtils.raycastFull(player, 25, false);

            if (!hitResult.getType().equals(HitResult.Type.ENTITY)) {
                return;
            }

            MetalCableEntity entity = new MetalCableEntity(
                    player.getWorld(),
                    (LivingEntity) ((EntityHitResult) hitResult).getEntity(),
                    player.getX(), player.getY(), player.getZ()
            );
            entity.setControlled(false);
            entity.createChain((LivingEntity) ((EntityHitResult) hitResult).getEntity(),2);
            entity.getTail().setOwner(player);
            bender.addBackgroundAbility(this,entity);

        }else{
            ((MetalCableEntity) bender.getBackgroundAbilityData(this)).despawn();
            bender.removeAbilityFromBackground(this);
        }
    }


    @Override
    public void onRemove(Bender bender) {
    }


    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        PlayerEntity player = bender.player;
        player.stopFallFlying();

    }
}
