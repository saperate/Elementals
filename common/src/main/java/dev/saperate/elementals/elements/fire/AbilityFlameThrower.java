package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFlameThrower implements Ability {
    public static final AABB boundingBox = new AABB(new Vec3(-1, -1, -1), new Vec3(1, 1, 1));

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("flameThrower")) {
            bender.setCurrAbility(null);
            return;
        }
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

        Player player = bender.player;
        if (bender.abilityData.equals(true)) {
            serverSummonParticles((ServerLevel) player.level(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, 0, 0, 0);
        } else {
            PlayerData plrData = PlayerData.get(player);

            int range = 6;
            if (plrData.canUseUpgrade("flameThrowerRangeII")) {
                range = 10;
            } else if (plrData.canUseUpgrade("flameThrowerRangeI")) {
                range = 8;
            }

            float damage = plrData.canUseUpgrade("blueFire") ? 3 : 2.5f;
            if (plrData.canUseUpgrade("flameThrowerDamageII")) {
                damage += 3f;
            } else if (plrData.canUseUpgrade("flameThrowerDamageI")) {
                damage += 1.5f;
            }

            int igniteSeconds = plrData.canUseUpgrade("flameThrowerIgniteI") ? 14 : 8;

            Vector3f pos = getEntityLookVector(player, 3).subtract(player.position()).normalize().scale(3).toVector3f();


            serverSummonParticles((ServerLevel) player.level(),
                    plrData.canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                    pos.x - 1,
                    pos.y - 1.6f,
                    pos.z - 1,
                    0.05f + player.getSpeed(), 8,
                    0, 0, 0, 2);
            playSoundAtEntity(player, SoundEvents.FIRE_AMBIENT,5);

            List<Entity> hits = player.level().getEntitiesOfClass(Entity.class,
                    boundingBox.inflate(range * 2).move(player.position()),
                    Entity::isAlive);

            for (Entity e : hits) {
                if (e.equals(player)  || e instanceof ItemEntity || e instanceof HangingEntity) {
                    continue;
                }
                if (SapsUtils.isLookingAt(bender.player, e, range, 0.75f)) {
                    if (!e.fireImmune()) {
                        e.igniteForSeconds(igniteSeconds);
                        e.hurt(e.damageSources().playerAttack(player), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
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