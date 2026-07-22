package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

/**
 * A single-cast forward wave: pulls a nearby water source and sends a surge of
 * water rushing outward in a cone in front of the caster, knocking every entity
 * in its path away and dealing a burst of damage - crowd control rather than the
 * focused single-target hits of Water Blade/Cannon.
 */
public class AbilityWaterRiptide implements Ability {
    private static final AABB boundingBox = new AABB(new Vec3(-1, -1, -1), new Vec3(1, 1, 1));

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("waterRiptide")) {
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(18)) {
            bender.setCurrAbility(null);
            return;
        }

        Player player = bender.player;
        Vector3f sourcePos = WaterElement.canBend(player, true);
        if (sourcePos == null) {
            bender.setCurrAbility(null);
            return;
        }

        PlayerData plrData = PlayerData.get(player);
        float range = plrData.canUseUpgrade("waterRiptideRangeI") ? 10 : 7;
        float damage = plrData.canUseUpgrade("waterRiptideDamageI") ? 5f : 3f;
        float waterMultiplier = WaterElement.getPowerMultiplier(player);

        Vec3 look = getEntityLookVector(player, 1).subtract(player.position()).normalize();

        serverSummonParticles((ServerLevel) player.level(),
                ParticleTypes.SPLASH, player, player.getRandom(),
                look.x * 2, 0.2, look.z * 2,
                0.15, 30,
                0, 0, 0, 3);
        player.level().playSound(null, player.getOnPos(), SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 1.2f,
                0.8f + (player.level().random.nextFloat() * 0.2f));

        List<Entity> hits = player.level().getEntitiesOfClass(Entity.class,
                boundingBox.inflate(range).move(player.position()),
                Entity::isAlive);

        for (Entity e : hits) {
            if (e.equals(player) || e instanceof ItemEntity || e instanceof HangingEntity) {
                continue;
            }
            Vec3 toEntity = e.position().subtract(player.position());
            double dist = toEntity.length();
            if (dist > range || dist < 0.001) {
                continue;
            }
            Vec3 dir = toEntity.normalize();
            //only hit things roughly in front of the caster - a forward cone, not a full circle
            double dot = look.dot(dir);
            if (dot < 0.5) {
                continue;
            }

            e.hurt(e.damageSources().playerAttack(player), damage * waterMultiplier * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
            e.addDeltaMovement(dir.scale(0.9 * waterMultiplier).add(0, 0.15, 0));
            e.hurtMarked = true;
        }

        //instant-cast: the wave crashes immediately and dissipates, nothing to hold or control
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}