package dev.saperate.elementals.client.entities.models.common;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SkyBisonSaddleModel extends DefaultedEntityGeoModel<SkyBisonEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "geo/entity/sky_bison_saddle.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/entity/sky_bison_saddle.png");
    private final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "animations/entity/sky_bison.animation.json");

    public SkyBisonSaddleModel() {
        super(ResourceLocation.fromNamespaceAndPath(Constants.MODID,"sky_bison"),true);
    }


    @Override
    public ResourceLocation getModelResource(SkyBisonEntity skyBisonEntity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SkyBisonEntity skyBisonEntity) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SkyBisonEntity skyBisonEntity) {
        return animation;
    }
}
