package dev.saperate.elementals.client.entities.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SkyBisonSaddleRenderLayer extends GeoRenderLayer<SkyBisonEntity> {
    
    public SkyBisonSaddleRenderLayer(GeoRenderer<SkyBisonEntity> entityRendererIn) {
        super(entityRendererIn);
    }


    @Override
    public void render(PoseStack poseStack, SkyBisonEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ((SkyBisonEntityRenderer)getRenderer()).saddleRenderer.render(animatable,animatable.yBodyRot,partialTick,poseStack,bufferSource,packedLight);
    }
}
