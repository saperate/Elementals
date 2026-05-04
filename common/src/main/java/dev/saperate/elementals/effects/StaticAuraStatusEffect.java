package dev.saperate.elementals.effects;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class StaticAuraStatusEffect extends MobEffect {

    public StaticAuraStatusEffect() {
        super(
                MobEffectCategory.BENEFICIAL,
                0x454545);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.isInWaterOrRain()){
            entity.removeEffect(ElementalsStatusEffects.STATIC_AURA);
        }
        return true;
    }

}
