package dev.saperate.elementals.client.features;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.GliderItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GliderFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final ItemRenderer itemRenderer;
    
    public GliderFeatureRenderer(RenderLayerParent<T, M> context, ItemRenderer itemRenderer) {
        super(context);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack stack = SapsUtils.getFirstItemOfTypeInHands((Player) entity, ElementalsItems.GLIDER_ITEM.get());
        if(stack.isEmpty() || !entity.isFallFlying() 
                || ElementalsItems.GLIDER_ITEM.get().getState(stack) == GliderItem.GliderStates.CLOSED){
            return;
        }
        matrices.pushPose();
        float scale = 0.8f;
        matrices.scale(scale,-scale,scale);
        matrices.translate(0,.5f,0);
        itemRenderer.renderStatic(
                stack, ItemDisplayContext.HEAD, 
                light, 0xFFFFFFFF, 
                matrices, vertexConsumers, 
                entity.level(), 0);
        matrices.popPose();
    }
}
