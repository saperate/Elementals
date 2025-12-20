package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.features.GliderFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
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
	

}