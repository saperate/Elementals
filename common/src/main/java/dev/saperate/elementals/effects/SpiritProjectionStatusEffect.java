package dev.saperate.elementals.effects;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.air.AbilityAir4;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import static net.minecraft.world.level.GameType.*;


public class SpiritProjectionStatusEffect extends MobEffect {

    public SpiritProjectionStatusEffect() {
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
        if(entity.level().isClientSide){
            return false;
        }
        if(entity instanceof Player player){
            Bender bender = Bender.getBender((ServerPlayer) player);
            if(!(bender.currAbility instanceof AbilityAir4)){
                //this is true when the world was closed before spirit projection could finish
                ((ServerPlayer) bender.player).changeGameMode(convertAmplifierToGameMode(amplifier));
                player.removeEffect(ElementalsStatusEffects.SPIRIT_PROJECTION);
            }
        }
        return true;
    }

    public static GameType convertAmplifierToGameMode(int amplifier){
        return switch (amplifier) {
            case 0 -> SURVIVAL;
            case 1 -> CREATIVE;
            case 2 -> SPECTATOR;
            case 3 -> ADVENTURE;
            default -> null;
        };
    }

    public static int convertGameModeToAmplifier(GameType mode){
        return switch (mode) {
            case SURVIVAL ->  0;
            case CREATIVE ->  1;
            case SPECTATOR -> 2;
            case ADVENTURE -> 3;
        };
    }

}
