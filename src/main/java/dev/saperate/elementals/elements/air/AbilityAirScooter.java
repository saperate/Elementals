package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.air.AirBallEntity;
import dev.saperate.elementals.entities.air.AirScooterEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityAirScooter implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.reduceChi(10);
        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        float speed = 0.25f;

        if (plrData.canUseUpgrade("airScooterSpeedII")) {
            speed = 0.75f;
        } else if (plrData.canUseUpgrade("airScooterSpeedI")) {
            speed = 0.5f;
        }

        AirScooterEntity entity = new AirScooterEntity(player.getWorld(), player);
        entity.setSpeed(speed);
        player.getWorld().spawnEntity(entity);
        player.startRiding(entity);

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
        bender.reduceChi(0.1f);
    }

    @Override
    public void onRemove(Bender bender) {

    }
}
