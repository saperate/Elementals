package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.GliderItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {
    @Shadow
    public abstract void startFallFlying();

    @Shadow
    public abstract void stopFallFlying();

    @Inject(at = @At("HEAD"), method = "causeFallDamage", cancellable = true)
    private void fall(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        Player player = ((Player) (Object) this);
        List<EarthBlockEntity> entities = player.level().getEntitiesOfClass(EarthBlockEntity.class,
                player.getBoundingBox().inflate(0.1f),
                EarthBlockEntity::canBeCollidedWith);

        if (entities.size() > 1) {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            Bender bender = Bender.getBender(serverPlayer);
            if (bender.ignoreNextFallDamage) {
                bender.ignoreNextFallDamage = false;
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }


    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        Player player = ((Player) (Object) this);
        if (safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION.get(), player)) {
            //checks if we are inside a wall
            float f = player.getDimensions(player.getPose()).width() * 0.8f;
            AABB box = AABB.ofSize(player.getEyePosition(), f, 1.0E-6, f);

            if (SapsUtils.checkBlockCollision(player, 0.1f, false, true, box) != null) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 21, 0, false, false, false));
            }
        }

        if (player.level().isClientSide) { // Below is serverside only
            return;
        }

        Bender bender = Bender.getBender((ServerPlayer) player);
        bender.tick();
        if (bender.castTime != null) {
            return;
        }

        if (bender.currAbility != null && !player.level().isClientSide) {
            bender.currAbility.onTick(bender);
        }
    }

    @Inject(at = @At("HEAD"), method = "blockActionRestricted", cancellable = true)
    private void restrictBlockBreaking(Level world, BlockPos pos, GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        Player player = ((Player) (Object) this);
        if (player instanceof ServerPlayer serverPlayer
                && Bender.getBender(serverPlayer).currAbility instanceof AbilityMetalDecoy) {
            cir.setReturnValue(true);
        }
    }

    //We inject right before minecraft checks if its an elytra, 
    // that way we still get the vanilla check + any mixins that may have come before
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"),
            method = "tryToStartFallFlying", cancellable = true)
    private void tryFallFlyingGlider(CallbackInfoReturnable<Boolean> cir) {
        Player player = ((Player) (Object) this);
        ItemStack stack = SapsUtils.getFirstItemOfTypeInHands(player, ElementalsItems.GLIDER_ITEM.get());
        if (!stack.isEmpty()
                && ElementalsItems.GLIDER_ITEM.get().getState(stack) == GliderItem.GliderStates.OPEN) {
            this.startFallFlying();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}