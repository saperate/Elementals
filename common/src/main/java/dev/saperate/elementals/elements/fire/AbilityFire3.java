package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFire3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (PlayerData.get(bender.player).canUseUpgrade("fireJump") && !bender.player.isInWater()) {
            bender.setCurrAbility(this);
            return;
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        if (bender.abilityData == null) {
            if (!bender.reduceChi(10)) {
                if (bender.abilityData == null) {
                    bender.setCurrAbility(null);
                } else {
                    onRemove(bender);
                }
                return;
            }
            bender.abilityData = false;
            Player player = bender.player;

            float power = 3;
            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("fireJumpRangeII")) {
                power = 5;
            } else if (plrData.canUseUpgrade("fireJumpRangeI")) {
                power = 3;
            }

            launchEntity(player, power);
        }
    }
    
    @Override
    public void onTick(Bender bender) {
        Player player = bender.player;
        int count = 1;
        if ((bender.abilityData == null || bender.abilityData.equals(true)) && player.isSprinting() && !player.onGround()
                && PlayerData.get(player).canUseUpgrade("fireJet")) {

            if (player.isUnderWater()) {
                onRemove(bender);
            }

            float power = 1.35f;
            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("fireJetSpeedII")) {
                power = 1.65f;
            } else if (plrData.canUseUpgrade("fireJetSpeedI")) {
                power = 1.5f;
            }

            player.startFallFlying();
            Vector3f velocity = getEntityLookVector(player, 2)
                    .subtract(player.getEyePosition())
                    .normalize().scale(power).toVector3f();
            player.setDeltaMovement(velocity.x, velocity.y, velocity.z);
            player.hurtMarked = true;
            player.move(MoverType.PLAYER, player.getDeltaMovement());
            bender.abilityData = true;
            player.fallDistance = 0;

            if (!bender.reduceChi(0.5f)) {
                if (bender.abilityData == null) {
                    bender.setCurrAbility(null);
                } else {
                    onRemove(bender);
                }
                return;
            }
            serverSummonParticles((ServerLevel) player.level(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 8,
                    0, -1.5f, 0, 0);
            return;
        } else if (bender.abilityData != null) {
            if (bender.abilityData.equals(true)) {
                onRemove(bender);
            } else if (bender.abilityData.equals(false)) {
                bender.abilityData = player.getRootVehicle().onGround();
                count = 8;
                if (player.isShiftKeyDown()) {
                    //bomb jump upgrade will enable it to be canceled
                    //bender.setCurrAbility(null);
                }
            }

        }

        serverSummonParticles((ServerLevel) player.level(),
                PlayerData.get(player).canUseUpgrade("blueFire") ?
                        ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, count,
                0, 0, 0, 0);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}
