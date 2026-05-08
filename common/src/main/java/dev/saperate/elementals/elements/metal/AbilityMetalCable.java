package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalCableEntity;
import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AbilityMetalCable implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        if(!bender.plrData.canUseUpgrade("metalCable"))
            return;
        
        if (deltaT > 500 && bender.isAbilityInBackground(this)) {
            Object data = bender.getBackgroundAbilityData(this);
            bender.setBackgroundAbilityData(this, packAbilityData(getEntity(data), !pullMode(data)));
            return;
        }

        if (!bender.isAbilityInBackground(this)) {
            PlayerData plrData = bender.plrData;
            int cost = 16;
            if (plrData.canUseUpgrade("metalCableEfficiencyII"))
                cost = 4;
            else if (plrData.canUseUpgrade("metalCableEfficiencyI"))
                cost = 9;
            if(!bender.reduceChi(10) || !MetalElement.canBend(bender.player, cost)){
                return;
            }
            
            Player player = bender.player;
            int range = 25;
            if(plrData.canUseUpgrade("metalCableRangeI")){
                range = 50;
            }
            HitResult hitResult = SapsUtils.raycastFull(player, range, false);
            
            if (!hitResult.getType().equals(HitResult.Type.BLOCK)) {
                bender.setCurrAbility(null);
                return;
            }

            MetalCableEntity entity = new MetalCableEntity(
                    player.level(),
                    player,
                    player.getX(), player.getY(), player.getZ()
            );
            player.level().addFreshEntity(entity);
            entity.setControlled(false);
            entity.createChain(player,1);
            entity.getTail().setFrozen(true);
            entity.getTail().setPosition(hitResult.getPos());
            bender.addBackgroundAbility(this, packAbilityData(entity, true));
            entity.setDistance(5f);
        } else {
            getEntity(bender.getBackgroundAbilityData(this)).despawn();
            bender.removeAbilityFromBackground(this);
        }
    }


    @Override
    public void onRemove(Bender bender) {
    }


    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        Player player = bender.player;
        player.stopFallFlying();

        MetalCableEntity entity = getEntity(data);
        if (player.isCrouching() && bender.plrData.canUseUpgrade("metalCablePrecisionI")) {
            entity.setDistance((float) Math.max(Math.min(entity.getDistance() + (pullMode(data) ? -0.1 : 0.1), 20), 0.1));
        }
    }

    public MetalCableEntity getEntity(Object abilityData) {
        return (MetalCableEntity) ((Object[])abilityData)[0];
    }

    public boolean pullMode(Object abilityData) {
        return (boolean) ((Object[])abilityData)[1];
    }

    public Object[] packAbilityData(MetalCableEntity entity, boolean pullMode) {
        return new Object[]{entity, pullMode};
    }
}
