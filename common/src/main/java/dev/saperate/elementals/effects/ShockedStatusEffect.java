package dev.saperate.elementals.effects;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ShockedStatusEffect extends MobEffect {

    public ShockedStatusEffect() {
        super(
                MobEffectCategory.HARMFUL,
                0x454545);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

}
