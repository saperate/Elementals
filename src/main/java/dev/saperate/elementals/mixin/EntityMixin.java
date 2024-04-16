package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.minecraft.world.entity.EntityChangeListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private Vec3d pos;

    @Shadow private EntityChangeListener changeListener;

    @Shadow private BlockPos blockPos;

    @Shadow @Nullable private BlockState blockStateAtPos;

    @Shadow private ChunkPos chunkPos;


    @Shadow public abstract void setVelocity(double x, double y, double z);

    @Shadow private Vec3d velocity;


    @Inject(at = @At("HEAD"), method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    private void setVelocity(Vec3d vel, CallbackInfo ci) {
        Entity entity = ((Entity)(Object) this);
        if(entity instanceof LivingEntity living){
            if(living.getPos().equals(new Vec3d(0,0,0))){//Really hacky way to check if we haven't been initialised yet. Surely this will never be an issue
                return;
            }
            if(living.hasStatusEffect(STATIONARY_EFFECT)){
                velocity = new Vec3d(0, -0.4f, 0);
                ci.cancel();
            }
        }
    }
}