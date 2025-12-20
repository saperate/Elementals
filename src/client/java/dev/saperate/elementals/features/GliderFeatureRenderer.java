package dev.saperate.elementals.features;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.utils.RenderUtils;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.items.GliderGeoModel;
import dev.saperate.elementals.items.glider.GliderItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class GliderFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final ItemRenderer itemRenderer;
    
    public GliderFeatureRenderer(FeatureRendererContext<T, M> context, ItemRenderer itemRenderer) {
        super(context);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack stack = SapsUtils.getFirstItemOfTypeInHands((PlayerEntity) entity, ElementalItems.GLIDER_ITEM);
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
                entity.getWorld(), 0);
        matrices.pop();
    }
}
