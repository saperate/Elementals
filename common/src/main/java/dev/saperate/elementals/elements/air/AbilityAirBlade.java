package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.*;

/**
 * A thin, fast blade of compressed air launched in a straight line. Unlike AirGust (a wide
 * cone that pushes everything back), this pierces through every entity in its narrow path,
 * dealing precise slashing damage with only a light shove instead of a big knockback.
 */
public class AbilityAirBlade implements Ability {
    public static final AABB boundingBox = new AABB(new Vec3(-1, -1, -1), new Vec3(1, 1, 1));

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("airBlade")) {
            bender.setCurrAbility(null);
            return;
        }

        if (!bender.reduceChi(8)) {
            bender.setCurrAbility(null);
            return;
        }

        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        int range = plrData.canUseUpgrade("airBladeRangeI") ? 16 : 10;
        float damage = plrData.canUseUpgrade("airBladeDamageI") ? 5f : 3f;

        Vector3f pos = getEntityLookVector(player, 3).subtract(player.position()).normalize().scale(3).toVector3f();

        serverSummonParticles((ServerLevel) player.level(),
                ParticleTypes.CLOUD, player, player.getRandom(),
                pos.x - 1,
                pos.y - 1.6f,
                pos.z - 1,
                0.35f + player.getSpeed(), 14,
                0, 0, 0, range / 2f);

        playSoundAtEntity(player, WIND_SOUND_EVENT, 5);

        List<Entity> hits = player.level().getEntitiesOfClass(Entity.class,
                boundingBox.inflate(range * 2).move(player.position()),
                Entity::isAlive);

        for (Entity e : hits) {
            if (e.equals(player) || e instanceof ItemEntity || e instanceof HangingEntity) {
                continue;
            }
            //narrow angle (0.95) so it behaves like a precise blade rather than a wide gust
            if (SapsUtils.isLookingAt(player, e, range, 0.95f)) {
                e.hurt(e.damageSources().playerAttack(player), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);

                Vec3 dir = e.position().subtract(player.position()).normalize();
                e.addDeltaMovement(dir.scale(0.15));
                e.hurtMarked = true;
            }
        }

        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}