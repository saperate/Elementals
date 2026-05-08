package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityAirJump implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(10)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        bender.setCurrAbility(null);
        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);
        float power = 2;

        if (plrData.canUseUpgrade("airJumpRangeII")) {
            power = 5;
        } else if (plrData.canUseUpgrade("airJumpRangeI")) {
            power = 3;
        }

        launchEntity(player,power, false);
        player.fallDistance = 0;

        serverSummonParticles((ServerLevel) player.level(),
                ParticleTypes.POOF, player, player.getRandom(),
                0, 0, 0,
                0.1f, 8,
                0, -1.5f, 0, 0);
    }
    
    @Override
    public void onRemove(Bender bender) {

    }

}
