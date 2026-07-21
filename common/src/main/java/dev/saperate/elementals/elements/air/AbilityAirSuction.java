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
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.playSoundAtEntity;

/**
 * The reverse of AirGust: while held (hold shift to sustain, same as AirShield), pulls every
 * entity within radius steadily toward the player - a localized vacuum - while also chipping
 * away real damage every few ticks, so it isn't just a repositioning tool anymore. The pull is
 * made visible with a continuous, concentrated beam of white particles running from the player
 * to every entity being dragged in.
 */
public class AbilityAirSuction implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("airSuction")) {
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
    public void onTick(Bender bender) {
        if (!bender.reduceChi(0.3f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);
        float radius = plrData.canUseUpgrade("airSuctionRangeI") ? 10 : 6;
        float damage = plrData.canUseUpgrade("airSuctionRangeI") ? 1.5f : 1f;

        SapsUtils.serverSummonParticles((ServerLevel) player.level(),
                ParticleTypes.CLOUD, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 2,
                0, 0, 0, 0);
        playSoundAtEntity(player, WIND_SOUND_EVENT, 8);

        List<Entity> targets = SapsUtils.getEntitiesInRadius(player.position(), radius, player.level(), player,
                e -> e.isAlive() && !(e instanceof ItemEntity) && !(e instanceof HangingEntity));

        for (Entity e : targets) {
            Vec3 dir = player.position().subtract(e.position());
            double distance = dir.length();
            if (distance < 1.2) {
                continue;
            }
            dir = dir.normalize();
            e.addDeltaMovement(dir.scale(0.12));
            e.hurtMarked = true;

            //continuous, concentrated beam of white particles from the player to the entity
            //being pulled - makes the vacuum visible instead of just an invisible force
            drawParticleBeam((ServerLevel) player.level(), player.getEyePosition(),
                    e.position().add(0, e.getBbHeight() / 2f, 0), player.getRandom());

            //real damage while being dragged in, applied periodically (like other bending
            //damage-over-time effects in this mod) so vanilla hit-invulnerability doesn't just
            //eat every tick's damage
            if (e.tickCount % 10 == 0) {
                e.hurt(e.damageSources().playerAttack(player), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
            }
        }

        if (!player.isShiftKeyDown()) {
            onRemove(bender);
        }
    }

    /**
     * Draws a short, dense line of concentrated white cloud particles between two points,
     * used to make the AirSuction pull visually read as a beam rather than ambient particles.
     */
    private void drawParticleBeam(ServerLevel level, Vec3 from, Vec3 to, net.minecraft.util.RandomSource rnd) {
        double distance = from.distanceTo(to);
        int steps = Math.max(2, (int) (distance * 2));
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vec3 point = from.lerp(to, t);
            level.sendParticles(ParticleTypes.CLOUD,
                    point.x + (rnd.nextDouble() - 0.5) * 0.08,
                    point.y + (rnd.nextDouble() - 0.5) * 0.08,
                    point.z + (rnd.nextDouble() - 0.5) * 0.08,
                    1, 0, 0, 0, 0);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}