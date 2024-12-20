package dev.saperate.elementals.effects;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.air.AbilityAir4;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import static net.minecraft.world.GameMode.*;

public class SpiritProjectionStatusEffect extends StatusEffect {

    public SpiritProjectionStatusEffect() {
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
        if(entity.getWorld().isClient){
            return false;
        }
        if(entity instanceof PlayerEntity player){
            Bender bender = Bender.getBender((ServerPlayerEntity) player);
            if(!(bender.currAbility instanceof AbilityAir4)){
                //this is true when the world was closed before spirit projection could finish
                ((ServerPlayerEntity) bender.player).changeGameMode(convertAmplifierToGameMode(amplifier));
                player.removeStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION);
            }
        }
        return true;
    }

    public static GameMode convertAmplifierToGameMode(int amplifier){
        return switch (amplifier) {
            case 0 -> SURVIVAL;
            case 1 -> CREATIVE;
            case 2 -> SPECTATOR;
            case 3 -> ADVENTURE;
            default -> null;
        };
    }

    public static int convertGameModeToAmplifier(GameMode mode){
        return switch (mode) {
            case SURVIVAL ->  0;
            case CREATIVE ->  1;
            case SPECTATOR -> 2;
            case ADVENTURE -> 3;
        };
    }

}
