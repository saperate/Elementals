package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import static dev.saperate.elementals.utils.SapsUtils.launchEntity;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterJump implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(10)) {
            onRemove(bender);
            return;
        }

        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        float power = 2;

        if (plrData.canUseUpgrade("waterJumpRangeII")) {
            power = 6;
        } else if (plrData.canUseUpgrade("waterJumpRangeI")) {
            power = 4;
        }

        launchEntity(player,power);
        player.fallDistance = 0;

        serverSummonParticles((ServerLevel) player.level(),
                ParticleTypes.SPLASH, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 4,
                0, -0.5f, 0, 0);
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

}
