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

/**
 * A short-range lashing strike of fire in front of the player, hitting every entity caught
 * in a narrow cone. Unlike FlameThrower (a sustained stream you hold), this is a single
 * instant "whip crack": high burst damage, ignites targets and knocks them away.
 */
public class AbilityFireWhip implements Ability {
    public static final AABB boundingBox = new AABB(new Vec3(-1, -1, -1), new Vec3(1, 1, 1));

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("fireWhip")) {
            bender.setCurrAbility(null);
            return;
        }

        if (!bender.reduceChi(12)) {
            bender.setCurrAbility(null);
            return;
        }

        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        int range = plrData.canUseUpgrade("fireWhipRangeI") ? 6 : 4;
        float damage = plrData.canUseUpgrade("fireWhipDamageI") ? 6f : 4f;
        int igniteSeconds = 5;

        Vector3f pos = getEntityLookVector(player, 3).subtract(player.position()).normalize().scale(3).toVector3f();

        serverSummonParticles((ServerLevel) player.level(),
                plrData.canUseUpgrade("blueFire") ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                player, player.getRandom(),
                pos.x - 1,
                pos.y - 1.6f,
                pos.z - 1,
                0.15f, 20,
                0, 0, 0, range / 2f);

        playSoundAtEntity(player, SoundEvents.FIRECHARGE_USE, 5);

        List<Entity> hits = player.level().getEntitiesOfClass(Entity.class,
                boundingBox.inflate(range * 2).move(player.position()),
                Entity::isAlive);

        for (Entity e : hits) {
            if (e.equals(player) || e instanceof ItemEntity || e instanceof HangingEntity) {
                continue;
            }
            if (SapsUtils.isLookingAt(player, e, range, 0.7f)) {
                if (!e.fireImmune()) {
                    e.igniteForSeconds(igniteSeconds);
                }
                e.hurt(e.damageSources().playerAttack(player), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);

                Vec3 dir = e.position().subtract(player.position()).normalize();
                e.addDeltaMovement(dir.scale(0.6).add(0, 0.15, 0));
                e.hurtMarked = true;
            }
        }

        //instant ability, nothing to hold onto afterwards
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}