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
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.include.com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

import static dev.saperate.elementals.Elementals.MODID;

public class DenseStatusEffect extends StatusEffect {
    public DenseStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x454545);
        addAttributeModifier(EntityAttributes.GENERIC_STEP_HEIGHT, Identifier.of(MODID,"dense_step"),0.4f, EntityAttributeModifier.Operation.ADD_VALUE);
        addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of(MODID,"dense_speed"),-0.20f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(EntityAttributes.GENERIC_JUMP_STRENGTH, Identifier.of(MODID,"dense_jump"),-0.5f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }


    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setSwimming(false);
        entity.setSprinting(false);
        //TODO make it so you can't jump in 1.20 either

        double currV = entity.getVelocity().y;
        if(entity.isTouchingWater()){
            currV -= 0.25f;
        }

        if(!entity.isOnGround())

        entity.setVelocity(new Vec3d(entity.getVelocity().x * 0.90, currV, entity.getVelocity().z * 0.90));
        return true;
    }

}
