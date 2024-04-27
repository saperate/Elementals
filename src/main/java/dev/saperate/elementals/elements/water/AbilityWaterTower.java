package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterTower implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;


        if (pos != null) {//If below us is water or we are touching water
            bender.abilityData = null;
            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
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
        serverSummonParticles((ServerWorld) player.getWorld(),
                ParticleTypes.BUBBLE, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 1,
                0, 0, 0, 0);


        //TODO check if there is still water below the player
    }

    @Override
    public void onRemove(Bender bender) {
        WaterJetEntity entity = (WaterJetEntity) bender.abilityData;
        bender.setCurrAbility(null);
        bender.abilityData = null;
        if (entity == null) {
            return;
        }
        entity.discard();
    }

}
