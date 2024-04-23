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

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityFire3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (PlayerData.get(bender.player).canUseUpgrade("fireAgility")) {
            bender.setCurrAbility(this);
            return;
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (bender.abilityData == null) {
            bender.abilityData = false;
            PlayerEntity player = bender.player;

            Vector3f velocity = getEntityLookVector(player, 1)
                    .subtract(player.getEyePos())
                    .normalize().multiply(1.75f).toVector3f();

            player.setVelocity(velocity.x,
                    velocity.y > 0 ? Math.min(velocity.y,1) : Math.max(velocity.y,-1),
                    velocity.z);
            player.velocityModified = true;
            player.move(MovementType.PLAYER, player.getVelocity());
        }
    }

    @Override
    public void onTick(Bender bender) {
        PlayerEntity player = bender.player;
        int count = 1;

        if ((bender.abilityData == null || bender.abilityData.equals(true)) && player.isSprinting()
        && PlayerData.get(player).canUseUpgrade("jet")) {
            player.startFallFlying();
            Vector3f velocity = getEntityLookVector(player, 2)
                    .subtract(player.getEyePos())
                    .normalize().multiply(0.65f).toVector3f();
            player.setVelocity(velocity.x, velocity.y, velocity.z);
            player.velocityModified = true;
            player.move(MovementType.PLAYER, player.getVelocity());
            bender.abilityData = true;


            serverSummonParticles((ServerWorld) player.getWorld(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 8,
                    0, -1.5f, 0, 0);
            return;
        } else if (bender.abilityData != null) {
            if (bender.abilityData.equals(true)) {
                bender.setCurrAbility(null);
            } else if (bender.abilityData.equals(false)) {
                bender.abilityData = player.isOnGround();
                count = 8;
                if(player.isSneaking()){
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

    }

}
