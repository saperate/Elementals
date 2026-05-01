package dev.saperate.elementals.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class StaticAuraStatusEffect extends StatusEffect {

    public StaticAuraStatusEffect() {
        super(
                StatusEffectCategory.BENEFICIAL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.isTouchingWaterOrRain()){
            entity.removeStatusEffect(ElementalsStatusEffects.STATIC_AURA);
        }
        return true;
    }

}
