package dev.saperate.elementals.mixin;

import com.mojang.brigadier.ParseResults;
import dev.saperate.elementals.blocks.LitAir;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
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
    @Shadow
    public abstract void remove(Entity.RemovalReason reason);

    @Inject(at = @At("HEAD"), method = "handleFallDamage", cancellable = true)
    private void fall(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        List<EarthBlockEntity> entities = player.getWorld().getEntitiesByClass(EarthBlockEntity.class,
                player.getBoundingBox().expand(0.1f),
                EarthBlockEntity::isCollidable);

        if(entities.size() > 1){
            cir.setReturnValue(false);
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
        if(player.getWorld().isClient){
            return;
        }
        Bender bender = Bender.getBender((ServerPlayerEntity) player);
        bender.tick();
        if (bender.castTime != null) {
            return;
        }

        if (bender.currAbility != null && !player.getWorld().isClient) {
            bender.currAbility.onTick(bender);
        }
    }


}