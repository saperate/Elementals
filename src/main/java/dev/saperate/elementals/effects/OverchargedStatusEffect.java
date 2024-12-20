package dev.saperate.elementals.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;


public class OverchargedStatusEffect extends StatusEffect {

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
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 4, 0, false, false, false));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 4, 0, false, false, false));
        return true;
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        //TODO add a mixin that will do this
        //entity.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.BURNOUT, 200 * (amplifier+1), amplifier, false, false, true));
    }


}
