package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.items.glider.GliderItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class GliderGeoModel extends GeoModel<GliderItem> {
    private final Identifier model = Identifier.of(Elementals.MODID, "geo/item/glider.geo.json");
    private final Identifier texture = Identifier.of(Elementals.MODID, "textures/item/glider_closed.png");
    private final Identifier animation = Identifier.of(Elementals.MODID, "animations/item/glider.animation.json");


    @Override
    public Identifier getModelResource(GliderItem gliderItem) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GliderItem gliderItem) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(GliderItem gliderItem) {
        return animation;
    }
}
