package dev.saperate.elementals.effects;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class StationaryStatusEffect extends StatusEffect {
    public static StationaryStatusEffect STATIONARY_EFFECT = new StationaryStatusEffect();

    public StationaryStatusEffect() {
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
        entity.slowMovement(Blocks.AIR.getDefaultState(), new Vec3d(0.00001f,1,0.00001f));
    }
}
