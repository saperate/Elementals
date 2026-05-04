package dev.saperate.elementals.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class StunnedStatusEffect extends MobEffect {

    public StunnedStatusEffect() {
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
        if(entity.tickCount % (amplifier >= 1 ? 3 : 4) == 0){
            //TODO blinking or seeing "stars" or blurry vision
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.00001f,1,0.00001f));
        }
        return true;
    }
}
