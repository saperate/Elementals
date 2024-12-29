package dev.saperate.elementals.effects;

import dev.saperate.elementals.mixin.StatusEffectInstanceAccessor;
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
        entity.setSprinting(false);
        if(entity.hasStatusEffect(ElementalsStatusEffects.OVERCHARGED)){
            entity.removeStatusEffect(ElementalsStatusEffects.OVERCHARGED);
        }
        if(!entity.hasStatusEffect(ElementalsStatusEffects.DENSE)){
            entity.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.DENSE, 8, 1, false, false, false));
        }else {
            ((StatusEffectInstanceAccessor) entity.getStatusEffect(ElementalsStatusEffects.DENSE)).setDuration(8);
        }
        return true;
    }


}
