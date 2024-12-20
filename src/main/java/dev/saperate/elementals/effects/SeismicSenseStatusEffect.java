package dev.saperate.elementals.effects;



import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;


public class SeismicSenseStatusEffect extends StatusEffect {
    public SeismicSenseStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x454545);
    }

    //TODO amplifier should extend range of detection
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 21, 0, false, false, false));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 21, 0, false, false, false));
        return true;
    }

}
