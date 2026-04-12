package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalBindEntity;
import dev.saperate.elementals.entities.metal.MetalBindEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AbilityMetalBind implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);


        if (!bender.isAbilityInBackground(this)) {
            PlayerData plrData = bender.plrData;

            int cost = 20;
            if (plrData.canUseUpgrade("metalBindEfficiencyII"))
                cost = 9;
            else if (plrData.canUseUpgrade("metalBindEfficiencyI"))
                cost = 16;

            if (!bender.reduceChi(10) || !MetalElement.canBend(bender.player, cost)) {
                return;
            }

            PlayerEntity player = bender.player;
            HitResult hitResult = SapsUtils.raycastFull(player, 10, false);

            if (!hitResult.getType().equals(HitResult.Type.ENTITY)) {
                return;
            }

            MetalBindEntity entity = new MetalBindEntity(
                    player.getWorld(),
                    (LivingEntity) ((EntityHitResult) hitResult).getEntity(),
                    player.getX(), player.getY(), player.getZ()
            );
            if (plrData.canUseUpgrade("metalBindRangeI")) {
                entity.setDistance(5);
            }
            entity.setControlled(false);
            entity.createChain((LivingEntity) ((EntityHitResult) hitResult).getEntity(), 2);
            entity.getTail().setOwner(player);
            bender.addBackgroundAbility(this, entity);

        } else {
            ((MetalBindEntity) bender.getBackgroundAbilityData(this)).despawn();
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
