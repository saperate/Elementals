package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.air.AirShieldEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;

public class AbilityAirShield implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.reduceChi(5);
        PlayerEntity player = bender.player;

        AirShieldEntity entity = new AirShieldEntity(player.getWorld(), player, player.getX(), player.getY(), player.getZ());
        bender.abilityData = entity;
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
        bender.reduceChi(0.15f);
        bender.player.addStatusEffect(new StatusEffectInstance(STATIONARY_EFFECT,1,1,false,false,false));
        if(!bender.player.isSneaking()){
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        AirShieldEntity entity = (AirShieldEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.discard();
    }

}
