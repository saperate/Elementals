package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SkyBisonSaddleRenderLayer extends GeoRenderLayer<SkyBisonEntity> {

    public SkyBisonSaddleRenderLayer(GeoRenderer<SkyBisonEntity> entityRendererIn) {
        super(entityRendererIn);
    }
}
