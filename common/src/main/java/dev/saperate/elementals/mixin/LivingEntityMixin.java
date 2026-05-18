package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.lightning.LightningElement;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.GliderItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.Nullable;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    @Nullable
    private DamageSource lastDamageSource;

    @Shadow
    protected float lastHurt;

    @Shadow
    protected int fallFlyTicks;

    @Inject(at = @At("TAIL"), method = "hurt")
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = ((LivingEntity) (Object) this);
        if (living.level().isClientSide) {
            return;
        }
        if (source.is(DamageTypes.MOB_ATTACK)
                || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)
                || source.is(DamageTypes.PLAYER_ATTACK)) {
            if (safeHasStatusEffect(ElementalsStatusEffects.STATIC_AURA.get(), living) && source.getDirectEntity() instanceof LivingEntity dmgSource) {
                dmgSource.addEffect(new MobEffectInstance(ElementalsStatusEffects.STUNNED.get(), 100, 1, false, true, true));
            }
        }


        if (source.is(DamageTypes.LIGHTNING_BOLT) && !safeHasStatusEffect(ElementalsStatusEffects.STATIC_AURA.get(), living)
                && living instanceof Player player && Bender.getBender((ServerPlayer) player).hasElement(LightningElement.get())) {
            float dmg = 0;
            if (safeHasStatusEffect(ElementalsStatusEffects.SHOCKED.get(), living)) {
                dmg = (float) living.getEffect(ElementalsStatusEffects.SHOCKED.get()).getAmplifier() / 10;
                living.removeEffect(ElementalsStatusEffects.SHOCKED.get());
            }
            if (lastDamageSource != null && lastDamageSource.is(DamageTypes.LIGHTNING_BOLT)) {
                dmg += lastHurt;
            }
            living.addEffect(new MobEffectInstance(ElementalsStatusEffects.SHOCKED.get(), 60, (int) (dmg * 10), false, true, true));
        }
    }

    @Inject(at = @At("HEAD"), method = "getDamageAfterArmorAbsorb", cancellable = true)
    private void applyArmor(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (source.is(DamageTypes.LIGHTNING_BOLT)) {
            cir.setReturnValue(amount);
            cir.cancel();
        }
    }


    @Inject(at = @At(value = "HEAD"), method = "getDamageAfterMagicAbsorb", cancellable = true)
    private void bypassesEnchantsAndEffect(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (source.is(DamageTypes.LIGHTNING_BOLT)) {
            if (safeHasStatusEffect(ElementalsStatusEffects.STATIC_AURA.get(), ((LivingEntity) (Object) this))) {
                cir.setReturnValue(amount / 2);
            } else {
                cir.setReturnValue(amount);
            }
            cir.cancel();
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "removeAllEffects")
    private void clearEffects(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = ((LivingEntity) (Object) this);
        //This is here because overcharged adds a status effect when it is removed.
        //If we allow it to do that while the method iterates through status effects,
        //it crashes because we are concurrently accessing it
        if (living.hasEffect(ElementalsStatusEffects.OVERCHARGED.get())) {
            living.removeEffect(ElementalsStatusEffects.OVERCHARGED.get());
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "onEffectRemoved")
    private void removeEffect(MobEffectInstance effect, CallbackInfo ci) {
        LivingEntity living = ((LivingEntity) (Object) this);

        if (effect.getEffect().equals(ElementalsStatusEffects.OVERCHARGED.get())) {
            living.addEffect(new MobEffectInstance(ElementalsStatusEffects.BURNOUT.get(), 200 * (effect.getAmplifier() + 1), effect.getAmplifier(), false, false, true));
        }
    }


    @Inject(at = @At(value = "HEAD"), method = "updateFallFlying", cancellable = true)
    private void fallFlying(CallbackInfo ci) {
        LivingEntity living = ((LivingEntity) (Object) this);
        if (living instanceof Player player) {
            ItemStack stack = SapsUtils.getFirstItemOfTypeInHands(player, ElementalsItems.GLIDER_ITEM.get());
            if (!stack.isEmpty() 
                    && ElementalsItems.GLIDER_ITEM.get().getState(stack) == GliderItem.GliderStates.OPEN) {
                int i = fallFlyTicks + 1;
                if (!player.level().isClientSide && i % 10 == 0) {
                    player.gameEvent(GameEvent.ELYTRA_GLIDE);
                }
                if (!player.level().isClientSide) {
                    player.startFallFlying();
                }
                ci.cancel();
            }
        }

    }
}