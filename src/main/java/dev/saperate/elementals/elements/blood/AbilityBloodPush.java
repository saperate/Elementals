package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.lightning.LightningElement;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class AbilityBloodPush implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        bender.setCurrAbility(null);

        HitResult hit = SapsUtils.raycastFull(
                player,
                20,
                false,
                (entity -> entity instanceof LivingEntity)
        );

        LivingEntity living = (LivingEntity) SapsUtils.entityFromHitResult(hit);
        if (living != null) {
            double x = (double) deltaT / 1000;
            int power = (int) Math.min(3, x * x + x + 1);

            Vector3f velocity = getEntityLookVector(player, 1)
                    .subtract(player.getEyePos())
                    .normalize().multiply(power, power * 0.5f, power).toVector3f();
            //returns the root vehicle or itself if there are none
            Entity vehicle = living.getRootVehicle();

            vehicle.setVelocity(velocity.x,
                    velocity.y,
                    velocity.z);
            vehicle.velocityModified = true;
            vehicle.move(MovementType.PLAYER, vehicle.getVelocity());
        }

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

    @Override
    public boolean shouldImmobilizePlayer() {
        return true;
    }

}
