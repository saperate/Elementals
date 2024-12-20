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
import net.minecraft.util.math.RotationAxis;

import static dev.saperate.elementals.Elementals.MODID;

public class BoomerangEntityRenderer extends EntityRenderer<BoomerangEntity> {
    private final ItemRenderer itemRenderer;


    public BoomerangEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }


    @Override
    public void render(BoomerangEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.time));

        this.itemRenderer.renderItem(entity.asItemStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId());

        matrices.pop();
        if(!entity.getInGround()){
            entity.time += 5;
        }

        if(entity.time >= 360){
            entity.time -= 360;
        }
    }

    @Override
    public Identifier getTexture(BoomerangEntity entity) {
        return Identifier.of(MODID, "textures/item/boomerang.png");
    }

}
