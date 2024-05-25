package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.earth.EarthElement;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityAirJump implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        //TODO make it more costly to cast when not on the ground
        bender.setCurrAbility(null);
        PlayerEntity player = bender.player;

        Vector3f velocity = getEntityLookVector(player, 1)
                .subtract(player.getEyePos())
                .normalize().multiply(2).toVector3f();

        player.setVelocity(velocity.x,
                velocity.y > 0 ? Math.min(velocity.y,1) : Math.max(velocity.y,-1),
                velocity.z);
        player.velocityModified = true;
        player.move(MovementType.PLAYER, player.getVelocity());
        player.fallDistance = 0;

        serverSummonParticles((ServerWorld) player.getWorld(),
                ParticleTypes.WHITE_SMOKE, player, player.getRandom(),
                0, 0, 0,
                0.1f, 8,
                0, -1.5f, 0, 0);
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

    }

    @Override
    public void onRemove(Bender bender) {

    }

}
