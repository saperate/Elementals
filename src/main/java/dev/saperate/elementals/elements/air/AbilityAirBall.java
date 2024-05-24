package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.air.AirBallEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityAirBall implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        //TODO remove 15 from every stuff like this to fix the velocity bug
        Vector3f pos = getEntityLookVector(player, 2.85f).toVector3f();

        AirBallEntity entity = new AirBallEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        bender.abilityData = entity;
        player.getWorld().spawnEntity(entity);

        bender.setCurrAbility(this);

    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        AirBallEntity entity = (AirBallEntity) bender.abilityData;
        if(entity == null){
            throw new RuntimeException("Elementals: Tried to launch entity while having none!");
        }
        onRemove(bender);

        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 0.75f, 0);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        AirBallEntity entity = (AirBallEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.discard();
        Bender.getBender((PlayerEntity) entity.getOwner()).setCurrAbility(null);
    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        AirBallEntity entity = (AirBallEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        Bender.getBender((PlayerEntity) entity.getOwner()).setCurrAbility(null);
    }
}
