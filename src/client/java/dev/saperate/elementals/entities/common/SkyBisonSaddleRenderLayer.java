package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SkyBisonSaddleRenderLayer extends GeoRenderLayer<SkyBisonEntity> {

    public SkyBisonSaddleRenderLayer(GeoRenderer<SkyBisonEntity> entityRendererIn) {
        super(entityRendererIn);
        
    }

    @Override
    public void render(MatrixStack poseStack, SkyBisonEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    @Override
    public GeoModel<SkyBisonEntity> getGeoModel() {
        return super.getGeoModel();
    }
}
