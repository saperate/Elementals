package dev.saperate.elementals.effects;

import dev.saperate.elementals.data.ElementalConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DrowningStatusEffect extends MobEffect {
    public DrowningStatusEffect() {
        super(
                MobEffectCategory.HARMFUL,
                0x454545);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.isUnderWater()){
            return false;
        }
        entity.setAirSupply(getNextAirUnderwater(entity.getAirSupply(),entity));
        if(entity.getAirSupply() <= -40) {
            entity.setAirSupply(-20);
            if (entity.level().isClientSide) {
                entity.level().playSound(entity, entity.getOnPos(),
                        SoundEvents.PLAYER_HURT_DROWN, SoundSource.PLAYERS,
                        4.0f,
                        (1.0f + (entity.level().random.nextFloat() - entity.level().random.nextFloat()) * 0.2f) * 0.7f
                );
            } else {
                entity.hurt(entity.damageSources().drown(), 1 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
            }
        }
        return true;
    }

    protected int getNextAirUnderwater(int air, LivingEntity entity) {
        AttributeInstance entityAttributeInstance = entity.getAttribute(Attributes.OXYGEN_BONUS);
        double d;
        if (entityAttributeInstance != null) {
            d = entityAttributeInstance.getValue();
        } else {
            d = 0.0;
        }

        return d > 0.0 && entity.getRandom().nextDouble() >= 1.0 / (d + 1.0) ? air : air - 5;
    }
}
