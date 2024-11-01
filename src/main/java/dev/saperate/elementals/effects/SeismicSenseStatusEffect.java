package dev.saperate.elementals.effects;

import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.MinecraftVersion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;

public class SeismicSenseStatusEffect extends StatusEffect {
    public SeismicSenseStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x454545);
    }

    //TODO amplifier should extend range of detection
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 21, 0, false, false, false));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 21, 0, false, false, false));
    }

}
