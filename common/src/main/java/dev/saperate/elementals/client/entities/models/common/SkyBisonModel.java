package dev.saperate.elementals.client.entities.models.common;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SkyBisonModel extends DefaultedEntityGeoModel<SkyBisonEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Elementals.MODID, "geo/entity/sky_bison.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Elementals.MODID, "textures/entity/sky_bison.png");
    private final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(Elementals.MODID, "animations/entity/sky_bison.animation.json");

    public SkyBisonModel() {
        super(ResourceLocation.fromNamespaceAndPath(Elementals.MODID,"sky_bison"),true);
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
