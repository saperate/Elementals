package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import dev.saperate.elementals.entities.models.common.SkyBisonModel;
import dev.saperate.elementals.entities.models.common.SkyBisonSaddleModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkyBisonSaddleRenderer extends GeoEntityRenderer<SkyBisonEntity> {
    public SkyBisonSaddleRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkyBisonSaddleModel());
    }

    @Override
    public void render(SkyBisonEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.hasSaddle()) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }
}
