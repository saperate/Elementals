package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.features.GliderFeatureRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>{


	@Shadow protected abstract boolean addFeature(FeatureRenderer<T, M> feature);

	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius, CallbackInfo ci) {
		LivingEntityRenderer e = ((LivingEntityRenderer) (Object) this);
		if(e instanceof PlayerEntityRenderer playerEntityRenderer){
			addFeature(new GliderFeatureRenderer(playerEntityRenderer, ctx.getItemRenderer()));
		}
	}

	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", cancellable = true)
	private void init(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if(livingEntity instanceof DecoyPlayerEntity && ClientBender.get().currAbility instanceof AbilityMetalDecoy){
			Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
			if(camera.getPos().distanceTo(livingEntity.getEyePos()) < 0.75){
				ci.cancel();
			}
		}
	}


}