package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.lightning.LightningElement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;
import static dev.saperate.elementals.effects.ShockedStatusEffect.SHOCKED_EFFECT;
import static dev.saperate.elementals.effects.StaticAuraStatusEffect.STATIC_AURA_EFFECT;
import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected float lastDamageTaken;

    @Shadow @Nullable private DamageSource lastDamageSource;


    @Inject(at = @At("TAIL"), method = "damage")
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = ((LivingEntity) (Object) this);
        if(living.getWorld().isClient){
            return;
        }
        if(source.isOf(DamageTypes.MOB_ATTACK)
                || source.isOf(DamageTypes.MOB_ATTACK_NO_AGGRO)
                || source.isOf(DamageTypes.PLAYER_ATTACK)){
            if(safeHasStatusEffect(STATIC_AURA_EFFECT,living) && source.getAttacker() instanceof LivingEntity dmgSource){
                dmgSource.addStatusEffect(new StatusEffectInstance(STUNNED_EFFECT,100, 1, false, true,true));
            }
        }


        if(source.isOf(DamageTypes.LIGHTNING_BOLT) && !safeHasStatusEffect(STATIC_AURA_EFFECT, living)
         && living instanceof PlayerEntity player && Bender.getBender((ServerPlayerEntity) player).hasElement(LightningElement.get())){
            float dmg = 0;
            if(safeHasStatusEffect(SHOCKED_EFFECT,living)){
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
            if(safeHasStatusEffect(STATIC_AURA_EFFECT,((LivingEntity) (Object) this))){
                cir.setReturnValue(amount/2);
            }else{
                cir.setReturnValue(amount);
            }
            cir.cancel();
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "clearStatusEffects")
    private void clearEffects(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = ((LivingEntity) (Object) this);
        //This is here because overcharged adds a status effect when it is removed.
        //If we allow it to do that while the method iterates through status effects,
        //it crashes because we are concurrently accessing it
        if(living.hasStatusEffect(OVERCHARGED_EFFECT)){
            living.removeStatusEffect(OVERCHARGED_EFFECT);
        }
    }
}