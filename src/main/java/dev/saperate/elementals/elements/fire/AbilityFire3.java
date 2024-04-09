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

    }

    @Override
    public void onTick(Bender bender) {
        PlayerEntity player = bender.player;

        if(bender.abilityData == null || (bender.abilityData.equals(true) && player.isSprinting())){
            if(player.isSprinting()){
                Vector3f velocity = getEntityLookVector(player,4).sub(player.getPos().toVector3f()).normalize(2);
                player.setVelocity(velocity.x,velocity.y,velocity.z);
                player.velocityModified = true;
                player.move(MovementType.PLAYER,player.getVelocity());
                bender.abilityData = true;
            }
        } else {
            bender.setCurrAbility(null);
        }

        serverSummonParticles((ServerWorld) player.getWorld(),
                PlayerData.get(player).canUseUpgrade("blueFire") ?
                        ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 1,
                0, 0, 0, 0);
    }

}
