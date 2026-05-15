package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.air.AirScooterEntity;
import net.minecraft.world.entity.player.Player;

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
        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        float speed = 0.5f;

        if (plrData.canUseUpgrade("airScooterSpeedII")) {
            speed = 0.85f;
        } else if (plrData.canUseUpgrade("airScooterSpeedI")) {
            speed = 0.65f;
        }

        AirScooterEntity entity = new AirScooterEntity(player.level(), player);
        entity.setSpeed(speed);
        player.level().addFreshEntity(entity);
        bender.setCurrAbility(this);
        bender.abilityData = entity;
    }

    @Override
    public void onTick(Bender bender) {
        if(bender.player.isShiftKeyDown()){
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
        if (bender.abilityData != null) {
            ((AirScooterEntity)bender.abilityData).discard();
        }
        bender.setCurrAbility(null);
    }
}
