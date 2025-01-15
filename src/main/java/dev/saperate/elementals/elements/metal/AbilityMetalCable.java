package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalCableEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class AbilityMetalCable implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(this);
        PlayerEntity player = bender.player;
        HitResult hitResult = SapsUtils.raycastFull(player,25,false);

        if(!hitResult.getType().equals(HitResult.Type.BLOCK)){
            bender.setCurrAbility(null);
            return;
        }

        MetalCableEntity entity = new MetalCableEntity(player.getWorld(), player, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
        entity.createChain(player);

        bender.abilityData = entity;
    }

    @Override
    public void onTick(Bender bender) {
        PlayerEntity player = bender.player;
        MetalCableEntity head = getAbilityData(bender.abilityData);
        if(player.isSneaking()){
            //onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        getAbilityData(bender.abilityData).despawn();
        bender.setCurrAbility(null);
    }

    public MetalCableEntity getAbilityData(Object object) {
        return (MetalCableEntity) object;
    }
}
