package dev.saperate.elementals.client.items;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.items.GliderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GliderItemRenderer extends GeoItemRenderer<GliderItem> {
    private final ResourceLocation textureOpen = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/item/glider_open.png");
    private final ResourceLocation textureClosed = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/item/glider_closed.png");
    public GliderItemRenderer() {
        super(new GliderGeoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(GliderItem animatable) {
        GliderItem.GliderStates state = animatable.getState(currentItemStack);
        
        switch (state){
            case OPEN -> {
                if(animatable.timeSinceStateChange(currentItemStack, Minecraft.getInstance().level) 
                        > 10.5){
                    return textureOpen;
                }
                return textureClosed;
            }
            case CLOSED-> {
                return textureClosed;
            }
            default -> {
                return super.getTextureLocation(animatable);
            }
        }
    }
}
