package dev.saperate.elementals.effects;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class StunnedStatusEffect extends StatusEffect {

    public StunnedStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.age % (amplifier >= 1 ? 3 : 4) == 0){
            entity.slowMovement(Blocks.AIR.getDefaultState(), new Vec3d(0.00001f,1,0.00001f));
        }
        return true;
    }
}
