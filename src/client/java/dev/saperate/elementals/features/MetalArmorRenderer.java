package dev.saperate.elementals.features;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.items.MetalArmorItem;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.DyeableGeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MetalArmorRenderer extends DyeableGeoArmorRenderer<MetalArmorItem> {
    public MetalArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(Elementals.MODID, "armor/metal_armor")));
    }

    @Override
    protected boolean isBoneDyeable(GeoBone geoBone) {
        return true;
    }

    @Override
    protected @NotNull Color getColorForBone(GeoBone geoBone) {
        return new Color(0xFF000000 | MetalArmorItem.getColor(currentStack));
    }
}
