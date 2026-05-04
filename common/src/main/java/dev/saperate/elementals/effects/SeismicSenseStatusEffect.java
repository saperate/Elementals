package dev.saperate.elementals.effects;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class SeismicSenseStatusEffect extends MobEffect {
    public SeismicSenseStatusEffect() {
        super(
                MobEffectCategory.NEUTRAL,
                0x454545);
    }

    //TODO amplifier should extend range of detection
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 21, 0, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 21, 0, false, false, false));
        return true;
    }

}
