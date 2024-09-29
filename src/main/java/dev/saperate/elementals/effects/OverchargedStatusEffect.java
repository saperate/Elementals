package dev.saperate.elementals.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import static dev.saperate.elementals.effects.BurnoutStatusEffect.BURNOUT_EFFECT;

public class OverchargedStatusEffect extends StatusEffect{
    public static OverchargedStatusEffect OVERCHARGED_EFFECT = new OverchargedStatusEffect();

    public OverchargedStatusEffect() {
        super(
                StatusEffectCategory.BENEFICIAL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 4, 0, false, false, false));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 4, 0, false, false, false));
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(BURNOUT_EFFECT, 200 * (amplifier+1), amplifier, false, false, true));
    }


}
