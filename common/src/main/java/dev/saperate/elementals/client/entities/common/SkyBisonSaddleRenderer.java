package dev.saperate.elementals.client.entities.common;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saperate.elementals.client.entities.models.common.SkyBisonSaddleModel;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkyBisonSaddleRenderer extends GeoEntityRenderer<SkyBisonEntity> {
    public SkyBisonSaddleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkyBisonSaddleModel());
    }

    @Override
    public void render(SkyBisonEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.hasSaddle()) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }
}
