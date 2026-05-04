package dev.saperate.elementals.effects;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class OverchargedStatusEffect extends MobEffect {

    public OverchargedStatusEffect() {
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
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 4, 0, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 4, 0, false, false, false));
        return true;
    }

}
