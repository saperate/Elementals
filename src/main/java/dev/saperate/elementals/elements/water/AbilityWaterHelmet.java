package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import dev.saperate.elementals.entities.water.WaterHelmetEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;

public class AbilityWaterHelmet implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        WaterHelmetEntity entity = new WaterHelmetEntity(player.getWorld(), player, player.getX(), player.getY(), player.getZ());
        player.getWorld().spawnEntity(entity);


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

    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

}
