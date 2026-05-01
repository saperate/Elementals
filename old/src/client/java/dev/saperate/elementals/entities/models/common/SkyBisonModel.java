package dev.saperate.elementals.entities.models.common;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SkyBisonModel extends DefaultedEntityGeoModel<SkyBisonEntity> {
    private final Identifier model = Identifier.of(Elementals.MODID, "geo/entity/sky_bison.geo.json");
    private final Identifier texture = Identifier.of(Elementals.MODID, "textures/entity/sky_bison.png");
    private final Identifier animation = Identifier.of(Elementals.MODID, "animations/entity/sky_bison.animation.json");

    public SkyBisonModel() {
        super(Identifier.of(Elementals.MODID,"sky_bison"),true);
    }


    @Override
    public Identifier getModelResource(SkyBisonEntity skyBisonEntity) {
        return model;
    }

    @Override
    public Identifier getTextureResource(SkyBisonEntity skyBisonEntity) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(SkyBisonEntity skyBisonEntity) {
        return animation;
    }
}
