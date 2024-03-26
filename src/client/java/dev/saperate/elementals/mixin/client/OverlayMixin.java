package dev.saperate.elementals.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class OverlayMixin {
	@Inject(at = @At("HEAD"), method = "renderFireOverlay", cancellable = true)
	private static void renderWaterOverlay(CallbackInfo info) {
		//info.cancel();
	}
}