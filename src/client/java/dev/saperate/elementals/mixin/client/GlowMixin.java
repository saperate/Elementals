package dev.saperate.elementals.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.text.html.parser.Entity;

@Mixin(LivingEntity.class)
public abstract class GlowMixin {



    @Inject(at = @At("HEAD"), method = "isGlowing", cancellable = true)
    private void render(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        LivingEntity e = ((LivingEntity) (Object) this);
        if (e.getWorld().isClient && e.isOnGround() && player != null && !player.equals(e) && e.getPos().subtract(player.getPos()).length() < 10) {
            //cir.setReturnValue(true);
        }
    }
}