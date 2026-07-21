package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
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
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

/**
 * The reverse of AirGust: while held (hold shift to sustain, same as AirShield), pulls every
 * entity within radius steadily toward the player - a localized vacuum. Doesn't damage
 * anything by itself; it's a setup tool to drag enemies into range, off a ledge, or into
 * another ability like FireWhip or AirBlade.
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

        serverSummonParticles((ServerLevel) player.level(),
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
        }

        if (!player.isShiftKeyDown()) {
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}