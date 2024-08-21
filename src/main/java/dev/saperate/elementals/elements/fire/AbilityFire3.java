package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFire3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (PlayerData.get(bender.player).canUseUpgrade("fireJump")) {
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
            PlayerEntity player = bender.player;

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
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {
        PlayerEntity player = bender.player;
        int count = 1;
        if ((bender.abilityData == null || bender.abilityData.equals(true)) && player.isSprinting() && !player.isOnGround()
                && PlayerData.get(player).canUseUpgrade("fireJet")) {

            if (player.isSubmergedInWater()) {
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
                    .subtract(player.getEyePos())
                    .normalize().multiply(power).toVector3f();
            player.setVelocity(velocity.x, velocity.y, velocity.z);
            player.velocityModified = true;
            player.move(MovementType.PLAYER, player.getVelocity());
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
            serverSummonParticles((ServerWorld) player.getWorld(),
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
                bender.abilityData = player.getRootVehicle().isOnGround();
                count = 8;
                if (player.isSneaking()) {
                    //bomb jump upgrade will enable it to be canceled
                    //bender.setCurrAbility(null);
                }
            }

        }

        serverSummonParticles((ServerWorld) player.getWorld(),
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
