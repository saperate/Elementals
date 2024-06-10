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
        bender.reduceChi(10);

        bender.setCurrAbility(null);
        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);
        float power = 2;

        if (plrData.canUseUpgrade("airJumpRangeII")) {
            power = 3;
        } else if (plrData.canUseUpgrade("airJumpRangeI")) {
            power = 2.5f;
        }
        launchPlayer(player,power);
        player.fallDistance = 0;

        serverSummonParticles((ServerWorld) player.getWorld(),
                ParticleTypes.POOF, player, player.getRandom(),
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
