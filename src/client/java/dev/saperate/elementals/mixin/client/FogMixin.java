package dev.saperate.elementals.mixin.client;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class FogMixin {



	@Inject(at = @At("HEAD"), method = "getSubmersionType", cancellable = true)
	private void render(CallbackInfoReturnable<CameraSubmersionType> cir) {
		cir.setReturnValue(CameraSubmersionType.NONE);
	}
}