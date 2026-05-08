package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityAirGust implements Ability {
    public static final AABB boundingBox = new AABB(new Vec3(-1, -1, -1), new Vec3(1, 1, 1));

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(5)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        bender.abilityData = true;
        bender.setCurrAbility(this);
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (started) {
            bender.abilityData = false;
        } else {
            onRemove(bender);
        }
    }

    @Override
    public void onTick(Bender bender) {
        if (bender.abilityData == null) {
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(0.25f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;
        if (bender.abilityData.equals(true)) {
            player.fallDistance -= 0.2f;
            serverSummonParticles((ServerLevel) player.level(),
                    ParticleTypes.CLOUD, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, 0, 0, 0);
        } else {
            Vec3 pos = getEntityLookVector(player, 3).subtract(player.position()).normalize().scale(3);


            serverSummonParticles((ServerLevel) player.level(),
                    ParticleTypes.POOF, player, player.getRandom(),
                    pos.x - 1,
                    pos.y - 1.6f,
                    pos.z - 1,
                    0.05f + player.getSpeed(), 12,
                    0, 0, 0, 2);
            playSoundAtEntity(player,WIND_SOUND_EVENT,5);

            List<Entity> hits = player.level().getEntitiesOfClass(Entity.class,
                    boundingBox.inflate(12).move(player.position()),
                    Entity::isAlive);

            float horizontalKnockback = -0.045f;

            //the reason why we make the y multiplier higher is because i makes the player "float"
            //which is more fun c:
            player.addDeltaMovement(pos.normalize().scale(horizontalKnockback));
            //reset it so we don't kill ourselves when we gently glide down
            if(player.getDeltaMovement().y <= -0.020f){
                player.fallDistance = 0;
            }
            player.hasImpulse = true;



            for (Entity e : hits) {
                if (e.equals(player) || e instanceof ItemEntity || e instanceof HangingEntity) {
                    continue;
                }
                Vec3 dir = player.position().subtract(e.position());
                if (dir.length() > 6) {
                    continue;
                }
                dir = dir.normalize();
                float dot = -pos.normalize().toVector3f().dot(dir.toVector3f());

                if (Math.cos(dot) <= 0.75 && dot >= 0) {
                    e.hurt(e.damageSources().playerAttack(player), 2.5f * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
                    e.addDeltaMovement(dir.scale(-0.1f));
                }
            }
        }
    }


    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
