package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.*;


public class AbilityBloodShield implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);

        if (bender.isAbilityInBackground(this)) {
            bender.removeAbilityFromBackground(this);
        } else if (bender.reduceChi(15)) {
            bender.addBackgroundAbility(this, 0);
        }
    }

    @Override
    public void onRemove(Bender bender) {
    }

    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        PlayerEntity player = bender.player;

        Random rnd = player.getRandom();
        SapsUtils.serverSummonParticles((ServerWorld) player.getWorld(), ParticleTypes.FISHING, player, player.getRandom(),
                rnd.nextBetween(-1,1), rnd.nextBetween(-1,1), rnd.nextBetween(-1,1),
                0.01,1,0f,(float) rnd.nextBetween(-25, (int) player.getHeight() * 100) / 100,0f,0f
        );


        List<Entity> hits = player.getWorld().getOtherEntities(
                player,
                player.getBoundingBox().expand(2.5),
                entity -> entity instanceof LivingEntity
        );

        if (!bender.reduceChi(0.125f + hits.size() * 0.1f)) {
            bender.removeAbilityFromBackground(this);
            return;
        }

        for (Entity entity : hits) {

            double distance = player.getPos().distanceTo(entity.getPos());
            double power = 1 / (distance - 0.3d);

            Vector3f velocity = entity.getPos()
                    .subtract(player.getPos())
                    .normalize().multiply(power, power * 0.5f, power).toVector3f();

            SapsUtils.serverSummonParticles((ServerWorld) player.getWorld(), ParticleTypes.FISHING, player, player.getRandom(),
                    velocity.normalize().x,velocity.normalize().y,velocity.normalize().z,
                    0.2,1,0f,player.getHeight()/5,0f,0f
            );
            
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
