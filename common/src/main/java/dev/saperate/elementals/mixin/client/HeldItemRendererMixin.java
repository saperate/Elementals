package dev.saperate.elementals.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.GliderItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin<T extends LivingEntity, M extends EntityModel<T>>{
	
	@Inject(at = @At("HEAD"), method = "renderItem", cancellable = true)
	private void renderItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int seed, CallbackInfo ci) {
		if(itemStack.is(ElementalsItems.GLIDER_ITEM.get()) 
				&& ElementalsItems.GLIDER_ITEM.get().getState(itemStack) == GliderItem.GliderStates.OPEN
				&& entity.isFallFlying()){
			ci.cancel();
		}
	}
	

}