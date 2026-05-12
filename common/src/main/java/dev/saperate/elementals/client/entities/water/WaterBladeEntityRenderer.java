package dev.saperate.elementals.client.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.client.entities.models.water.WaterBladeModel;
import dev.saperate.elementals.entities.water.WaterBladeEntity;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

import static dev.saperate.elementals.client.ElementalsClient.MODEL_WATER_BLADE_LAYER;

public class WaterBladeEntityRenderer extends EntityRenderer<WaterBladeEntity> implements RenderLayerParent<WaterBladeEntity, WaterBladeModel> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/entity/water.png");
    private final WaterBladeModel model;
    public static long firstTime = -1;

    public WaterBladeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new WaterBladeModel(context.bakeLayer(MODEL_WATER_BLADE_LAYER));
    }

    @Override
    public void render(WaterBladeEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime);
        matrices.pushPose();
        matrices.translate(0, -1.42125f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.itemEntityTranslucentCull(getTextureLocation(entity)));

        int color = BiomeColors.getAverageWaterColor(entity.level(),entity.getOnPos());
        matrices.mulPose(Axis.YP.rotationDegrees(rot * 20));

        WaterBladeModel.getTexturedModelData().bakeRoot().render(
                matrices,vertexConsumer,light,0,
                0x88000000 | color
        );




        RenderSystem.disableBlend();
        matrices.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(WaterBladeEntity entity) {
        return texture;
    }

    @Override
    public WaterBladeModel getModel() {
        return this.model;
    }



}
