package dev.saperate.elementals.client.entities.common;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.entities.common.BoomerangEntity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class BoomerangEntityRenderer extends EntityRenderer<BoomerangEntity> {
    private final ItemRenderer itemRenderer;


    public BoomerangEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }


    @Override
    public void render(BoomerangEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();

        matrices.mulPose(Axis.XP.rotationDegrees(90));
        matrices.mulPose(Axis.ZP.rotationDegrees(entity.time));

        this.itemRenderer.renderStatic(entity.getPickupItem(), ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, entity.level(), entity.getId());

        matrices.popPose();
        if(!entity.getInGround()){
            entity.time += 5;
        }

        if(entity.time >= 360){
            entity.time -= 360;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BoomerangEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/item/boomerang.png");
    }

}
