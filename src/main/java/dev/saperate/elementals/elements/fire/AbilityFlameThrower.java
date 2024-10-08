package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFlameThrower implements Ability {
    public static final Box boundingBox = new Box(new Vec3d(-1, -1, -1), new Vec3d(1, 1, 1));

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
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

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
        if (!bender.reduceChi(0.15f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        if (bender.abilityData == null) {
            bender.setCurrAbility(null);
            return;
        }

        PlayerEntity player = bender.player;
        if (bender.abilityData.equals(true)) {
            serverSummonParticles((ServerWorld) player.getWorld(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, 0, 0, 0);
        } else {
            Vector3f pos = getEntityLookVector(player, 3).subtract(player.getPos()).normalize().multiply(3).toVector3f();


            serverSummonParticles((ServerWorld) player.getWorld(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                    pos.x - 1,
                    pos.y - 1.6f,
                    pos.z - 1,
                    0.05f + player.getMovementSpeed(), 8,
                    0, 0, 0, 2);
            playSoundAtEntity(player,SoundEvents.BLOCK_FIRE_AMBIENT,5);

            List<Entity> hits = player.getWorld().getEntitiesByClass(Entity.class,
                    boundingBox.expand(12).offset(player.getPos()),
                    Entity::isAlive);

            for (Entity e : hits) {
                if (e.equals(player)  || e instanceof ItemEntity || e instanceof AbstractDecorationEntity) {
                    continue;
                }
                if (SapsUtils.isLookingAt(bender.player,e,6,0.75f)) {
                    if (!e.isFireImmune()) {
                        e.setOnFireFor(8);
                        e.damage(e.getDamageSources().playerAttack(player), PlayerData.get(player).canUseUpgrade("blueFire") ? 3 : 2.5f);
                    }
                }
            }
        }
    }


    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
