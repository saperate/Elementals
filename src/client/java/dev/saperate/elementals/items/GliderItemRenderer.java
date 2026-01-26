package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class GliderItemRenderer extends GeoItemRenderer<GliderItem> {
    private final Identifier textureOpen = Identifier.of(Elementals.MODID, "textures/item/glider_open.png");
    private final Identifier textureClosed = Identifier.of(Elementals.MODID, "textures/item/glider_closed.png");
    public GliderItemRenderer() {
        super(new GliderGeoModel());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        
        super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public Identifier getTextureLocation(GliderItem animatable) {
        GliderItem.GliderStates state = animatable.getState(currentItemStack);
        
        
        switch (state){
            case OPEN -> {
                if(animatable.timeSinceStateChange(currentItemStack) > 10.5){
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
