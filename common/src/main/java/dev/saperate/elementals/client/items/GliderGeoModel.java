package dev.saperate.elementals.client.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.items.GliderItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GliderGeoModel extends GeoModel<GliderItem> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Elementals.MODID, "geo/item/glider.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Elementals.MODID, "textures/item/glider_closed.png");
    private final ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(Elementals.MODID, "animations/item/glider.animation.json");


    @Override
    public ResourceLocation getModelResource(GliderItem gliderItem) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GliderItem gliderItem) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GliderItem gliderItem) {
        return animation;
    }
}
