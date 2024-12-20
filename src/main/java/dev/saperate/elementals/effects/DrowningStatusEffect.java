package dev.saperate.elementals.effects;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class DrowningStatusEffect extends StatusEffect {
    public DrowningStatusEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.isSubmergedInWater()){
            return false;
        }
        entity.setAir(getNextAirUnderwater(entity.getAir(),entity));
        if(entity.getAir() <= -40) {
            entity.setAir(-20);
            if (entity.getWorld().isClient) {
                entity.getWorld().playSound(entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.ENTITY_PLAYER_HURT_DROWN, SoundCategory.PLAYERS,
                        4.0f,
                        (1.0f + (entity.getWorld().random.nextFloat() - entity.getWorld().random.nextFloat()) * 0.2f) * 0.7f,
                        false
                );
            } else {
                entity.damage(entity.getDamageSources().drown(), 1);
            }
        }
        return true;
    }

    protected int getNextAirUnderwater(int air, LivingEntity entity) {
        EntityAttributeInstance entityAttributeInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_OXYGEN_BONUS);
        double d;
        if (entityAttributeInstance != null) {
            d = entityAttributeInstance.getValue();
        } else {
            d = 0.0;
        }

        return d > 0.0 && entity.getRandom().nextDouble() >= 1.0 / (d + 1.0) ? air : air - 4;//TODO tweak this value
    }
}
