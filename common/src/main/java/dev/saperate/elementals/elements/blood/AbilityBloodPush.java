package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class AbilityBloodPush implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        bender.setCurrAbility(null);

        HitResult hit = SapsUtils.raycastFull(
                player,
                20,
                false,
                (entity -> entity instanceof LivingEntity)
        );

        LivingEntity living = (LivingEntity) SapsUtils.entityFromHitResult(hit);
        if (living != null && bender.reduceChi(10)) {
            double x = (double) deltaT / 1000;
            int power = (int) Math.min(
                    bender.plrData.canUseUpgrade("bloodPushPowerI") ? 4 : 2.5f,
                    x * x + x + 1
            );

            Vector3f velocity = getEntityLookVector(player, 1)
                    .subtract(player.getEyePosition())
                    .normalize().multiply(power, power * 0.5f, power).toVector3f();
            //returns the root vehicle or itself if there are none
            Entity vehicle = living.getRootVehicle();

            vehicle.setDeltaMovement(velocity.x,
                    velocity.y,
                    velocity.z);
            vehicle.hasImpulse = true;
            vehicle.move(MoverType.PLAYER, vehicle.getDeltaMovement());

        }

    }


    @Override
    public void onRemove(Bender bender) {

    }

    @Override
    public boolean shouldImmobilizePlayer(Player player) {
        return true;
    }

}
