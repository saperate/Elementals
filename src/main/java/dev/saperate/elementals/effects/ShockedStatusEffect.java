package dev.saperate.elementals.effects;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.air.AbilityAir4;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import static net.minecraft.world.GameMode.*;

public class ShockedStatusEffect extends StatusEffect {
    public static ShockedStatusEffect SHOCKED_EFFECT = new ShockedStatusEffect();

    public ShockedStatusEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

}
