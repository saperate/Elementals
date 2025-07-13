package dev.saperate.elementals.effects;

import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.mixin.FurnaceBlockEntityAccessor;
import dev.saperate.elementals.network.ModMessages;
import dev.saperate.elementals.utils.SapsUtils;
import dev.saperate.elementals.utils.SapsUtils;
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
import net.minecraft.util.hit.HitResult;
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


        double currV = entity.getVelocity().y;
        if (((ElementalsLivingEntityAccessor) entity).isJumping() && entity.isOnGround() && entity.isSubmergedInWater()) {
            currV += 2;
        } else {
            currV = -0.25;
        }

        
        //Prevent slow fall when midair
        HitResult hit = SapsUtils.raycastBlockCustomRotation(entity,2.5f,false,new Vec3d(0,-1,0));
        if(!entity.isOnGround() && hit.getType().equals(HitResult.Type.MISS) && !entity.isTouchingWater()){
            return true;
        }

        entity.setVelocity(new Vec3d(entity.getVelocity().x * 0.90, currV, entity.getVelocity().z * 0.90));
        return true;
    }

}
