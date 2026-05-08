package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.misc.StunExplosion;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;

public class AbilityLightningEMP implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {//TODO FIX FOR NPCS THX MO
        if (!bender.reduceChi(15)) {
            return;
        }
        bender.setCurrAbility(null);
        Player player = bender.player;
        SapsUtils.serverSummonParticles((ServerLevel) player.level(),
                LIGHTNING_PARTICLE_TYPE, player, player.getRandom(), -0.5,-0.5,-0.5,0.75f,100,0,0,0,1);
        StunExplosion explosion = new StunExplosion(player.level(), player, player.getX(), player.getY(), player.getZ(), 2.5f, false, Explosion.BlockInteraction.KEEP, 4 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER,0, player);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }


    @Override
    public void onRemove(Bender bender) {
    }
}
