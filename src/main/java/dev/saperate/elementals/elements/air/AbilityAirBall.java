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
        if (!bender.reduceChi(20)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        PlayerEntity player = bender.player;

        //TODO remove 0.15 from every stuff like this to fix the velocity bug
        Vector3f pos = getEntityLookVector(player, 2).toVector3f();

        AirBallEntity entity = new AirBallEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        bender.abilityData = entity;
        player.getWorld().spawnEntity(entity);

        bender.setCurrAbility(this);

    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        AirBallEntity entity = (AirBallEntity) bender.abilityData;
        onRemove(bender);
        if(entity == null){
            return;
        }
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 0.75f;
        if (plrData.canUseUpgrade("airBallSpeedII")) {
            speed = 1.75f;
        } else if (plrData.canUseUpgrade("airBallSpeedI")) {
            speed = 1.25f;
        }

        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 0);
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
        AirBallEntity entity = (AirBallEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        bender.setCurrAbility(null);
    }
}
