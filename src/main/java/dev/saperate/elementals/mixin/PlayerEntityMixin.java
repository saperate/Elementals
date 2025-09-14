package dev.saperate.elementals.mixin;

import com.mojang.datafixers.util.Either;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private boolean gliderStartedGlidingState = false; 
    
    @Shadow
    public abstract void remove(Entity.RemovalReason reason);

    @Shadow
    public abstract void startFallFlying();

    @Shadow public abstract void stopFallFlying();

    @Shadow public abstract Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos);

    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void fall(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        List<EarthBlockEntity> entities = player.getWorld().getEntitiesByClass(EarthBlockEntity.class,
                player.getBoundingBox().expand(0.1f),
                EarthBlockEntity::isCollidable);

        if (entities.size() > 1) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "checkFallFlying", cancellable = true)
    private void fallFlying(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (player.getMainHandStack().isOf(ElementalItems.GLIDER_ITEM)
                || player.getOffHandStack().isOf(ElementalItems.GLIDER_ITEM)) {

            boolean shouldStartFlying = !player.isOnGround() && !player.isFallFlying() //Vanilla check
                    && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION);
            if (shouldStartFlying) {
                startFallFlying();
                gliderStartedGlidingState = true;
            }else{
                stopFallFlying();
            }
            cir.setReturnValue(shouldStartFlying);
            cir.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION, player)) {
            //checks if we are inside a wall
            float f = player.getDimensions(player.getPose()).width() * 0.8f;
            Box box = Box.of(player.getEyePos(), f, 1.0E-6, f);

            if (SapsUtils.checkBlockCollision(player, 0.1f, false, true, box) != null) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 21, 0, false, false, false));
            }
        }
        if (player.getWorld().isClient) { // Below is serverside only
            return;
        }

        boolean shouldStartFlying = !player.isOnGround() && !player.isFallFlying() //Vanilla check
                && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION);


        
        Bender bender = Bender.getBender((ServerPlayerEntity) player);
        bender.tick();
        if (bender.castTime != null) {
            return;
        }

        if (bender.currAbility != null && !player.getWorld().isClient) {
            bender.currAbility.onTick(bender);
        }
    }

    @Inject(at = @At("HEAD"), method = "isBlockBreakingRestricted", cancellable = true)
    private void restrictBlockBreaking(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (player instanceof ServerPlayerEntity serverPlayer
                && Bender.getBender(serverPlayer).currAbility instanceof AbilityMetalDecoy) {
            cir.setReturnValue(true);
        }
    }

}