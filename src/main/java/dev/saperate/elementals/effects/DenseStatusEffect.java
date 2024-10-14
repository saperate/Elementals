package dev.saperate.elementals.effects;

import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.mixin.FurnaceBlockEntityAccessor;
import dev.saperate.elementals.network.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.include.com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

public class DenseStatusEffect extends StatusEffect {
    public static DenseStatusEffect DENSE_EFFECT = new DenseStatusEffect();

    public DenseStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x454545);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if(entity.getStepHeight() < 1) {
            if (entity instanceof ServerPlayerEntity player) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(entity.getStepHeight() + 0.4f);
                ServerPlayNetworking.send(player, ModMessages.UPDATE_PLAYER_STEP_HEIGHT, buf);
            }
            entity.setStepHeight(entity.getStepHeight() + 0.4f);
        }
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setSwimming(false);
        if (amplifier >= 10 && entity.isOnGround() && !entity.isSubmergedInWater()) {
            entity.slowMovement(Blocks.AIR.getDefaultState(), new Vec3d(1.25, 0.1, 1.25));
        }

        entity.setSwimming(false);


        double currV = entity.getVelocity().y;
        if (((ElementalsLivingEntityAccessor) entity).isJumping() && entity.isOnGround() && entity.isSubmergedInWater()) {
            currV += 2;
        } else {
            currV = -0.25;
        }


        entity.setVelocity(new Vec3d(entity.getVelocity().x * 0.90, currV, entity.getVelocity().z * 0.90));
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if(entity.getStepHeight() >= 1){
            if(entity instanceof ServerPlayerEntity player){
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(entity.getStepHeight() - 0.4f);
                ServerPlayNetworking.send(player, ModMessages.UPDATE_PLAYER_STEP_HEIGHT, buf);
            }
            entity.setStepHeight(entity.getStepHeight() - 0.4f);
        }
    }
}
