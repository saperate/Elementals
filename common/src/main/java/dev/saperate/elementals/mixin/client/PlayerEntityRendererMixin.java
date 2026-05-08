package dev.saperate.elementals.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin {
	
	@Inject(at = @At("TAIL"), method = "setModelProperties")
	private void modelPose(AbstractClientPlayer player, CallbackInfo ci) {
		//Quick dirty bug fix
		PlayerRenderer plrRenderer = ((PlayerRenderer)(Object) this);
		plrRenderer.getModel().crouching = player.isCrouching();
	}


}