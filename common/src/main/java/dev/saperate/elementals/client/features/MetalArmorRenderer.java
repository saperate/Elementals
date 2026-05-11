package dev.saperate.elementals.client.features;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.items.MetalArmorItem;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

public class MetalArmorRenderer extends DyeableGeoArmorRenderer<MetalArmorItem> {
    public MetalArmorRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Elementals.MODID, "armor/metal_armor")));
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
