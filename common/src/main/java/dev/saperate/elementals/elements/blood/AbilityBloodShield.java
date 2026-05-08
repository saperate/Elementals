package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;


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
        Player player = bender.player;

        RandomSource rnd = player.getRandom();
        SapsUtils.serverSummonParticles((ServerLevel) player.level(), ParticleTypes.FISHING, player, player.getRandom(),
                rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble(),
                0.01,1,0f,(float) rnd.nextInt(-25, (int) player.getBbHeight() * 100) / 100,0f,0f
        );


        List<Entity> hits = player.level().getEntities(
                player,
                player.getBoundingBox().inflate(2.5),
                entity -> entity instanceof LivingEntity
        );

        if (!bender.reduceChi(0.125f + hits.size() * 0.1f)) {
            bender.removeAbilityFromBackground(this);
            return;
        }

        for (Entity entity : hits) {

            double distance = player.position().distanceTo(entity.position());
            double power = 1 / (distance - 0.3d);

            Vector3f velocity = entity.position()
                    .subtract(player.position())
                    .normalize().multiply(power, power * 0.5f, power).toVector3f();

            SapsUtils.serverSummonParticles((ServerLevel) player.level(), ParticleTypes.FISHING, player, player.getRandom(),
                    velocity.normalize().x,velocity.normalize().y,velocity.normalize().z,
                    0.2,1,0f,player.getBbHeight()/5,0f,0f
            );
            
            //returns the root vehicle or itself if there are none
            Entity vehicle = entity.getRootVehicle();

            vehicle.addDeltaMovement(new Vec3(velocity.x, velocity.y, velocity.z));
            vehicle.hasImpulse = true;
            vehicle.move(MoverType.PLAYER, vehicle.getDeltaMovement());
        }
    }
}
