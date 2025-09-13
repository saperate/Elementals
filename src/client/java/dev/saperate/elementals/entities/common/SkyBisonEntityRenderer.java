package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import dev.saperate.elementals.entities.models.common.SkyBisonModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkyBisonEntityRenderer extends GeoEntityRenderer<SkyBisonEntity> {
    public final SkyBisonSaddleRenderer saddleRenderer;
    
    public SkyBisonEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkyBisonModel());
        saddleRenderer = new SkyBisonSaddleRenderer(renderManager);
        addRenderLayer(new SkyBisonSaddleRenderLayer(this));
        withScale(5);
    }
}
