package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.utils.ClientUtils.safeHasStatusEffect;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {



	@Inject(at = @At("TAIL"), method = "setModelPose")
	private void modelPose(AbstractClientPlayerEntity player, CallbackInfo ci) {
		//Quick dirty bug fix
		PlayerEntityRenderer plrRenderer = ((PlayerEntityRenderer)(Object) this);
		plrRenderer.getModel().sneaking = player.isInSneakingPose() || player.isSneaking();
	}

}