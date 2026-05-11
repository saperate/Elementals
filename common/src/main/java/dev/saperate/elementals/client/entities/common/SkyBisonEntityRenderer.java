package dev.saperate.elementals.client.entities.common;

import dev.saperate.elementals.client.entities.models.common.SkyBisonModel;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkyBisonEntityRenderer extends GeoEntityRenderer<SkyBisonEntity> {
    public final SkyBisonSaddleRenderer saddleRenderer;
    
    public SkyBisonEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkyBisonModel());
        saddleRenderer = new SkyBisonSaddleRenderer(renderManager);
        addRenderLayer(new SkyBisonSaddleRenderLayer(this));
        withScale(5);
    }
}
