package dev.saperate.elementals.entities.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class DirtBottleEntityRenderer extends EntityRenderer<DirtBottleEntity> {
    private final ItemRenderer itemRenderer;


    public DirtBottleEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }


    @Override
    public void render(DirtBottleEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.translate(0.0D, 0.15D, 0.0D);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(0.75F, 0.75F, 0.75F);

        this.itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId());

        matrices.pop();
    }

    @Override
    public Identifier getTexture(DirtBottleEntity entity) {
        return Identifier.of(MODID, "textures/item/dirt_bottle.png");
    }

}
