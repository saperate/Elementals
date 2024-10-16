package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.lightning.VoltArcEntity;
import dev.saperate.elementals.misc.FireExplosion;
import dev.saperate.elementals.misc.StunExplosion;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AbilityLightningEMP implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(15)) {
            return;
        }
        bender.setCurrAbility(null);
        PlayerEntity player = bender.player;
        SapsUtils.serverSummonParticles((ServerWorld) player.getWorld(),
                LIGHTNING_PARTICLE_TYPE, player, player.getRandom(), -0.5,-0.5,-0.5,0.75f,100,0,0,0,1);
        StunExplosion explosion = new StunExplosion(player.getWorld(), player, player.getX(), player.getY(), player.getZ(), 2.5f, false, Explosion.DestructionType.KEEP, 4,0, player);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
    }
}
