package dev.saperate.elementals.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;


public class BurnoutStatusEffect extends MobEffect {
    public BurnoutStatusEffect() {
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
        entity.setSprinting(false);
        if(entity.hasEffect(ElementalsStatusEffects.OVERCHARGED.get())){
            entity.removeEffect(ElementalsStatusEffects.OVERCHARGED.get());
        }
        entity.addEffect(new MobEffectInstance(ElementalsStatusEffects.DENSE.get(), 8, 1, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 8, amplifier, false, false, false));
        return true;
    }


}
