package dev.saperate.elementals.effects;

import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.mixin.FurnaceBlockEntityAccessor;
import net.minecraft.block.Blocks;
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
        if(amplifier >= 10){
            entity.slowMovement(Blocks.COBWEB.getDefaultState(), new Vec3d(0.8f,3.1f,0.8f));
            return;
        }


        double currV = entity.getVelocity().y;

        if (((ElementalsLivingEntityAccessor) entity).isJumping() && entity.isOnGround() && entity.isSubmergedInWater()) {
            currV += 0.8f * amplifier;
        } else {
            currV -= 0.07f * amplifier;
        }
        entity.setSwimming(false);


        entity.setVelocity(new Vec3d(entity.getVelocity().x, currV, entity.getVelocity().z));
    }
}
