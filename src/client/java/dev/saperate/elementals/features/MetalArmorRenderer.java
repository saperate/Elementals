package dev.saperate.elementals.features;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.items.MetalArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MetalArmorRenderer extends GeoArmorRenderer<MetalArmorItem> {
    public MetalArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(Elementals.MODID, "armor/metal_armor")));
    }
}
