package dev.saperate.elementals.effects;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DenseStatusEffect extends MobEffect {
    public DenseStatusEffect() {
        super(
                MobEffectCategory.NEUTRAL,
                0x454545);
        addAttributeModifier(Attributes.STEP_HEIGHT, ResourceLocation.fromNamespaceAndPath(Constants.MODID,"dense_step"),0.4f, AttributeModifier.Operation.ADD_VALUE);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(Constants.MODID,"dense_speed"),-0.20f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(Constants.MODID,"dense_jump"),-0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }


    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setSwimming(false);
        entity.setSprinting(false);


        double currV = entity.getDeltaMovement().y;
        if (((ElementalsLivingEntityAccessor) entity).isJumping() && entity.onGround() && entity.isUnderWater()) {
            currV += 2;
        } else {
            currV = -0.25;
        }

        
        //Prevent slow fall when midair
        HitResult hit = SapsUtils.raycastBlockCustomRotation(entity,2.5f,false,new Vec3(0,-1,0));
        if(!entity.onGround() && hit.getType().equals(HitResult.Type.MISS) && !entity.isInWater()){
            return true;
        }

        entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x * 0.90, currV, entity.getDeltaMovement().z * 0.90));
        return true;
    }

}
