package dev.saperate.elementals.mixin;

import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.ShockedStatusEffect.SHOCKED_EFFECT;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected float lastDamageTaken;

    @Shadow @Nullable private DamageSource lastDamageSource;

    @Inject(at = @At("TAIL"), method = "damage")
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity e = ((Entity) (Object) this);
        if(e instanceof LivingEntity living && source.isOf(DamageTypes.LIGHTNING_BOLT)){
            float dmg = 0;
            if(SapsUtils.safeHasStatusEffect(SHOCKED_EFFECT,living)){
                dmg = (float) living.getStatusEffect(SHOCKED_EFFECT).getAmplifier() / 10;
                living.removeStatusEffect(SHOCKED_EFFECT);
            }
            if(lastDamageSource != null && lastDamageSource.isOf(DamageTypes.LIGHTNING_BOLT)){
                dmg += lastDamageTaken;
            }
            living.addStatusEffect(new StatusEffectInstance(SHOCKED_EFFECT,60,(int)(dmg * 10),false,true,true));
        }
    }

    @Inject(at = @At("HEAD"), method = "applyArmorToDamage", cancellable = true)
    private void applyArmor(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if(source.isOf(DamageTypes.LIGHTNING_BOLT)){
            cir.setReturnValue(amount);
            cir.cancel();
        }
    }


    @Inject(at = @At(value = "HEAD"), method = "modifyAppliedDamage", cancellable = true)
    private void bypassesEnchantsAndEffect(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if(source.isOf(DamageTypes.LIGHTNING_BOLT)){
            cir.setReturnValue(amount);
            cir.cancel();
        }
    }
}