package dev.saperate.elementals.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;


public class BurnoutStatusEffect extends StatusEffect {
    public BurnoutStatusEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.hasStatusEffect(ElementalsStatusEffects.OVERCHARGED)){
            entity.removeStatusEffect(ElementalsStatusEffects.OVERCHARGED);
        }
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 8, amplifier, false, false, false));
        entity.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.DENSE, 4, 1, false, false, false));
        return true;
    }


}
