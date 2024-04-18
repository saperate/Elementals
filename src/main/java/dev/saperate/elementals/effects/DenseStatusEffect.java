package dev.saperate.elementals.effects;

import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.mixin.FurnaceBlockEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.Vec3d;

public class DenseStatusEffect extends StatusEffect {
    public static DenseStatusEffect DENSE_EFFECT = new DenseStatusEffect();

    public DenseStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        double currV = entity.getVelocity().y;

        if (((ElementalsLivingEntityAccessor) entity).isJumping()) {
            currV += 0.035f;
        } else {
            currV -= 0.07f;
        }
        entity.setSwimming(false);
        entity.setMovementSpeed(2);

        entity.setVelocity(new Vec3d(entity.getVelocity().x, currV, entity.getVelocity().z));
    }
}
