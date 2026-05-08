package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityAirGust implements Ability {
    public static final Box boundingBox = new Box(new Vec3(-1, -1, -1), new Vec3(1, 1, 1));

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
            serverSummonParticles((ServerWorld) player.level(),
                    ParticleTypes.CLOUD, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, 0, 0, 0);
        } else {
            Vec3 pos = getEntityLookVector(player, 3).subtract(player.getPos()).normalize().multiply(3);


            serverSummonParticles((ServerWorld) player.level(),
                    ParticleTypes.POOF, player, player.getRandom(),
                    pos.x - 1,
                    pos.y - 1.6f,
                    pos.z - 1,
                    0.05f + player.getMovementSpeed(), 12,
                    0, 0, 0, 2);
            playSoundAtEntity(player,WIND_SOUND_EVENT,5);

            List<Entity> hits = player.level().getEntitiesByClass(Entity.class,
                    boundingBox.expand(12).offset(player.getPos()),
                    Entity::isAlive);

            float horizontalKnockback = -0.045f;

            //the reason why we make the y multiplier higher is because i makes the player "float"
            //which is more fun c:
            player.addDeltaMovement(pos.normalize().multiply(horizontalKnockback));
            //reset it so we don't kill ourselves when we gently glide down
            if(player.getDeltaMovement().y <= -0.020f){
                player.fallDistance = 0;
            }
            player.velocityModified = true;



            for (Entity e : hits) {
                if (e.equals(player) || e instanceof ItemEntity || e instanceof AbstractDecorationEntity) {
                    continue;
                }
                Vec3 dir = player.getPos().subtract(e.getPos());
                if (dir.length() > 6) {
                    continue;
                }
                dir = dir.normalize();
                float dot = -pos.normalize().toVector3f().dot(dir.toVector3f());

                if (Math.cos(dot) <= 0.75 && dot >= 0) {
                    e.hurt(e.damageSources().playerAttack(player), 2.5f * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
                    e.addDeltaMovement(dir.multiply(-0.1f));
                }
            }
        }
    }


    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
