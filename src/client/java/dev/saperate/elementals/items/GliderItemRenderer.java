package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.items.glider.GliderItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GliderItemRenderer extends GeoItemRenderer<GliderItem> {
    private final Identifier textureOpen = Identifier.of(Elementals.MODID, "textures/item/glider_open.png");
    private final Identifier textureClosed = Identifier.of(Elementals.MODID, "textures/item/glider_closed.png");
    public GliderItemRenderer() {
        super(new GliderGeoModel());
    }

    

    @Override
    public Identifier getTextureLocation(GliderItem animatable) {
        GliderItem item = ((GliderItem) currentItemStack.getItem());
        GliderItem.GliderStates state = item.getState(currentItemStack);
        
        switch (state){
            case OPEN -> {
                return textureOpen;
            }
            case OPENING, CLOSED, CLOSING -> {
                return textureClosed;
            }
            default -> {
                return super.getTextureLocation(animatable);
            }
        }
    }
}
