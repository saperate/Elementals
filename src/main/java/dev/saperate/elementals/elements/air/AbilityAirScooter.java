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
        if (!bender.reduceChi(10)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        float speed = 0.5f;

        if (plrData.canUseUpgrade("airScooterSpeedII")) {
            speed = 0.85f;
        } else if (plrData.canUseUpgrade("airScooterSpeedI")) {
            speed = 0.65f;
        }

        AirScooterEntity entity = new AirScooterEntity(player.getWorld(), player);
        entity.setSpeed(speed);
        player.getWorld().spawnEntity(entity);
        bender.setCurrAbility(this);
        bender.abilityData = entity;
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
        if(bender.player.isSneaking()){
            ((AirScooterEntity)bender.abilityData).discard();
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(0.1f) || ((AirScooterEntity)bender.abilityData).isRemoved() ) {
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }
}
