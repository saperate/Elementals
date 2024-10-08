package dev.saperate.elementals.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ShockedStatusEffect extends StatusEffect {
    public static ShockedStatusEffect SHOCKED_EFFECT = new ShockedStatusEffect();

    public ShockedStatusEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

}
