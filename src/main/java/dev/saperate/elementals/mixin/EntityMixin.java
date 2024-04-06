package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;

@Mixin(Entity.class)
public abstract class EntityMixin {


    @Shadow public abstract void readNbt(NbtCompound nbt);

    @Inject(at = @At("HEAD"), method = "setPos", cancellable = true)
    private void setPos(CallbackInfo ci) {
        Entity entity = ((Entity)(Object) this);
        if(entity instanceof LivingEntity living){
            if(living.getPos().equals(new Vec3d(0,0,0))){
                return;
            }
            if(living.hasStatusEffect(STATIONARY_EFFECT)){
                ci.cancel();
            }
        }
    }
}