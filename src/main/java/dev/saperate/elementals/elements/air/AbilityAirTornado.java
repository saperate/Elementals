package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.air.AirShieldEntity;
import dev.saperate.elementals.entities.air.AirTornadoEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;

public class AbilityAirTornado implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        PlayerEntity player = bender.player;

        AirTornadoEntity entity = new AirTornadoEntity(player.getWorld(), player, player.getX(), player.getY(), player.getZ());
        PlayerData plrData = PlayerData.get(player);
        float speed = 0.01f;

        if (plrData.canUseUpgrade("airTornadoSpeedII")) {
            speed = 0.05f;
        } else if (plrData.canUseUpgrade("airTornadoSpeedI")) {
            speed = 0.02f;
        }
        entity.setSpeed(speed);
        entity.setControlled(true);
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
        if (!bender.reduceChi(0.1f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
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
        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 0.2f, 0);
        entity.setVelocity(entity.getVelocity().multiply(1, 0, 1));//We can't fling tornadoes upwards
        entity.maxLifeTime = 120;
        entity.setStepHeight(1f);
    }
}
