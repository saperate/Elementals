package dev.saperate.elementals.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class StationaryStatusEffect extends StatusEffect {
    public static StationaryStatusEffect STATIONARY_EFFECT = new StationaryStatusEffect();
    public StationaryStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setMovementSpeed(0);
        entity.setVelocity(0,0,0);
    }
}