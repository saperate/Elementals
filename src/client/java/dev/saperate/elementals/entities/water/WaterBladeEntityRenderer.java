package dev.saperate.elementals.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.models.water.WaterBladeModel;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.ElementalsClient.MODEL_WATER_BLADE_LAYER;

public class WaterBladeEntityRenderer extends EntityRenderer<WaterBladeEntity> implements FeatureRendererContext<WaterBladeEntity,WaterBladeModel> {
    private static final Identifier texture = new Identifier(MODID, "textures/entity/water.png");
    private final WaterBladeModel model;
    public static long firstTime = -1;

    public WaterBladeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        model = new WaterBladeModel(context.getPart(MODEL_WATER_BLADE_LAYER));
    }

    @Override
    public void render(WaterBladeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime);
        matrices.push();
        matrices.translate(0, -1.42125f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(getTexture(entity)));

        int color = BiomeColors.getWaterColor(entity.getWorld(),entity.getBlockPos());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot * 20));

        WaterBladeModel.getTexturedModelData().createModel().render(
                matrices,vertexConsumer,light,0,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.5f
        );




        RenderSystem.disableBlend();
        matrices.pop();

    }

    @Override
    public Identifier getTexture(WaterBladeEntity entity) {
        return texture;
    }

    @Override
    public WaterBladeModel getModel() {
        return this.model;
    }



}
