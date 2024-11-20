package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.utils.ClientUtils.safeHasStatusEffect;

@Mixin(LivingEntity.class)
public abstract class GlowMixin {


    @Shadow
    public abstract void remove(Entity.RemovalReason reason);

    @Inject(at = @At("HEAD"), method = "isGlowing", cancellable = true)
    private void render(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        LivingEntity e = ((LivingEntity) (Object) this);
        if (safeHasStatusEffect(ElementalsStatusEffects.SEISMIC_SENSE, player) && e.getWorld().isClient && e.isOnGround()
                && player.isOnGround()
                && !player.equals(e)
                && e.getPos().subtract(player.getPos()).length() <= 60) {
            cir.setReturnValue(true);
        }
    }
}