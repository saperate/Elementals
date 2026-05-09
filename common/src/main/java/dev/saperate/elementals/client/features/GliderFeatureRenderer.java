package dev.saperate.elementals.client.features;

import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.items.GliderItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.PoseStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;

public class GliderFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final ItemRenderer itemRenderer;
    
    public GliderFeatureRenderer(FeatureRendererContext<T, M> context, ItemRenderer itemRenderer) {
        super(context);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack stack = SapsUtils.getFirstItemOfTypeInHands((Player) entity, ElementalItems.GLIDER_ITEM);
        if(stack.isEmpty() || !entity.isFallFlying() 
                || ElementalItems.GLIDER_ITEM.getState(stack) == GliderItem.GliderStates.CLOSED){
            return;
        }
        matrices.push();
        float scale = 0.8f;
        matrices.scale(scale,-scale,scale);
        matrices.translate(0,.5f,0);
        
        itemRenderer.renderItem(
                stack, ModelTransformationMode.HEAD, 
                light, 0xFFFFFFFF, 
                matrices, vertexConsumers, 
                entity.level(), 0);
        matrices.pop();
    }
}
