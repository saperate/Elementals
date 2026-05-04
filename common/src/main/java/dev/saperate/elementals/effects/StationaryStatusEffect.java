package dev.saperate.elementals.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class StationaryStatusEffect extends MobEffect {
    public StationaryStatusEffect() {
        super(
                MobEffectCategory.NEUTRAL,
                0x454545);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.00001f,1,0.00001f));
        return true;
    }
}
