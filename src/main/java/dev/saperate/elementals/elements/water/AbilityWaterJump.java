package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import static dev.saperate.elementals.utils.SapsUtils.launchEntity;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterJump implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(15)) {
            onRemove(bender);
        }

        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        float power = 1;

        if (plrData.canUseUpgrade("waterJumpRangeII")) {
            power = 2;
        } else if (plrData.canUseUpgrade("waterJumpRangeI")) {
            power = 1.5f;
        }

        launchEntity(player,power);
        player.fallDistance = 0;

        serverSummonParticles((ServerWorld) player.getWorld(),
                ParticleTypes.SPLASH, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 4,
                0, -0.5f, 0, 0);
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

    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

}
