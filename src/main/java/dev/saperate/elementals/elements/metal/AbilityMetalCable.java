package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalCableEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class AbilityMetalCable implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(this);
        PlayerEntity player = bender.player;
        HitResult hitResult = SapsUtils.raycastFull(player, 25, false);

        if (!hitResult.getType().equals(HitResult.Type.ENTITY)) {
            bender.setCurrAbility(null);
            return;
        }

        MetalCableEntity entity = new MetalCableEntity(
                player.getWorld(),
                (LivingEntity) ((EntityHitResult) hitResult).getEntity(),
                player.getX(), player.getY(), player.getZ()
        );
        entity.setControlled(false);
        entity.createChain((LivingEntity) ((EntityHitResult) hitResult).getEntity());
        entity.getTail().setOwner(player);

        bender.abilityData = entity;
    }

    @Override
    public void onTick(Bender bender) {
        PlayerEntity player = bender.player;
        MetalCableEntity head = getCableEntity(bender.abilityData);
        MetalCableEntity tail = head.getTail();

        if (player.isSneaking()) {
            if(tail.getParent() != head.getChild()){
                tail.getParent().setPos(tail.getX(),tail.getY(),tail.getZ());
                tail.remove();
            }else {
                onRemove(bender);
            }
        }
    }

    @Override
    public void onRemove(Bender bender) {
        getCableEntity(bender.abilityData).despawn();
        bender.setCurrAbility(null);
    }


    public MetalCableEntity getCableEntity(Object abilityData) {
        return (MetalCableEntity) abilityData;
    }

}
