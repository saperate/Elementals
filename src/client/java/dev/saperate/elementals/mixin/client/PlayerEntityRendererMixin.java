package dev.saperate.elementals.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {



	@Inject(at = @At("TAIL"), method = "setModelPose")
	private void modelPose(AbstractClientPlayerEntity player, CallbackInfo ci) {
		//Quick dirty bug fix
		PlayerEntityRenderer plrRenderer = ((PlayerEntityRenderer)(Object) this);
		plrRenderer.getModel().sneaking = player.isInSneakingPose() || player.isSneaking();
	}

}