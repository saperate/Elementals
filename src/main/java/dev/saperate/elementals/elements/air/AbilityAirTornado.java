package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.air.AirShieldEntity;
import dev.saperate.elementals.entities.air.AirTornadoEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;

public class AbilityAirTornado implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.reduceChi(15);
        PlayerEntity player = bender.player;

        AirTornadoEntity entity = new AirTornadoEntity(player.getWorld(), player, player.getX(), player.getY(), player.getZ());
        PlayerData plrData = PlayerData.get(player);
        float speed = 0.001f;

        if (plrData.canUseUpgrade("airTornadoSpeedII")) {
            speed = 0.005f;
        } else if (plrData.canUseUpgrade("airTornadoSpeedI")) {
            speed = 0.002f;
        }
        entity.setSpeed(speed);
        bender.abilityData = entity;
        entity.setOwner(player);
        player.getWorld().spawnEntity(entity);


        bender.setCurrAbility(this);
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
        if (!bender.player.isSneaking()) {
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        AirTornadoEntity entity = (AirTornadoEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, .25f, 0);
        entity.setVelocity(entity.getVelocity().multiply(1, 0, 1));//We can't fling tornadoes upwards
        entity.setStepHeight(1f);
    }
}
