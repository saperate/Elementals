package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class AbilityBloodShield implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);

        if(bender.isAbilityInBackground(this)){
            bender.removeAbilityFromBackground(this);
        }else {
            bender.addBackgroundAbility(this,0);
        }
    }

    @Override
    public void onRemove(Bender bender) {
    }

    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        PlayerEntity player = bender.player;

        List<Entity> hits = player.getWorld().getOtherEntities(
                player,
                player.getBoundingBox().expand(2.5),
                entity -> entity instanceof LivingEntity
        );

        for(Entity entity : hits){//TODO make it so the more entities there are the more it takes chi

            double distance = player.getPos().distanceTo(entity.getPos());
            double power = 1/(distance - 0.3d);

            Vector3f velocity = entity.getPos()
                    .subtract(player.getPos())
                    .normalize().multiply(power, power * 0.5f, power).toVector3f();
            //returns the root vehicle or itself if there are none
            Entity vehicle = entity.getRootVehicle();

            vehicle.addVelocity(velocity.x,
                    velocity.y,
                    velocity.z);
            vehicle.velocityModified = true;
            vehicle.move(MovementType.PLAYER, vehicle.getVelocity());
        }
    }
}
