package dev.saperate.elementals.entities.models.common;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SkyBisonSaddleModel extends DefaultedEntityGeoModel<SkyBisonEntity> {
    private final Identifier model = Identifier.of(Elementals.MODID, "geo/entity/sky_bison_saddle.geo.json");
    private final Identifier texture = Identifier.of(Elementals.MODID, "textures/entity/sky_bison_saddle.png");
    private final Identifier animation = Identifier.of(Elementals.MODID, "animations/entity/sky_bison.animation.json");

    public SkyBisonSaddleModel() {
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
