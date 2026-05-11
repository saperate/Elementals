package dev.saperate.elementals.client.entities.common;

import dev.saperate.elementals.entities.common.DirtBottleEntity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import static dev.saperate.elementals.Elementals.MODID;

public class DirtBottleEntityRenderer extends EntityRenderer<DirtBottleEntity> {
    private final ItemRenderer itemRenderer;


    public DirtBottleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }


    @Override
    public void render(DirtBottleEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();

        matrices.translate(0.0D, 0.15D, 0.0D);
        matrices.mulPose(entity.getDirection().getRotation());
        matrices.scale(0.75F, 0.75F, 0.75F);

        this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, entity.level(), entity.getId());

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DirtBottleEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/item/dirt_bottle.png");
    }

}
