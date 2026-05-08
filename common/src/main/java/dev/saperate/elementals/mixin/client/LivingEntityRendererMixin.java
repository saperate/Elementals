package dev.saperate.elementals.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.client.features.GliderFeatureRenderer;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>{

	@Shadow
	public abstract boolean addLayer(RenderLayer<T, M> p_115327_);

	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(EntityRendererProvider.Context context, EntityModel model, float shadowRadius, CallbackInfo ci) {
		LivingEntityRenderer e = ((LivingEntityRenderer) (Object) this);
		if(e instanceof PlayerRenderer playerEntityRenderer){
			addLayer(new GliderFeatureRenderer(playerEntityRenderer, context.getItemRenderer()));
		}
	}

	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
	private void init(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		if(entity instanceof DecoyPlayerEntity && ClientBender.get().currAbility instanceof AbilityMetalDecoy){
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			if(camera.getPosition().distanceTo(entity.getEyePosition()) < 0.75){
				ci.cancel();
			}
		}
	}


}