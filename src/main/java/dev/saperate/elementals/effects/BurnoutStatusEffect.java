package dev.saperate.elementals.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;

public class BurnoutStatusEffect extends StatusEffect {
    public static BurnoutStatusEffect BURNOUT_EFFECT = new BurnoutStatusEffect();

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
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.hasStatusEffect(OVERCHARGED_EFFECT)){
            entity.removeStatusEffect(OVERCHARGED_EFFECT);
        }
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 8, amplifier, false, false, false));
        entity.addStatusEffect(new StatusEffectInstance(DENSE_EFFECT, 4, 1, false, false, false));
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    }
}
