package dev.saperate.elementals.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {



	@Inject(at = @At("TAIL"), method = "setModelPose")
	private void render(AbstractClientPlayerEntity player, CallbackInfo ci) {
		//Quick dirty bug fix, it doesn't
		((PlayerEntityRenderer)(Object) this).getModel().sneaking = player.isInSneakingPose() || player.isSneaking();
	}
}