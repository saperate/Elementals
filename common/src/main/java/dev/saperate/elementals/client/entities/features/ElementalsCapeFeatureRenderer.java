package dev.saperate.elementals.client.entities.features;

import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.PoseStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ElementalsCapeFeatureRenderer extends FeatureRenderer<DecoyPlayerEntity, PlayerEntityModel<DecoyPlayerEntity>> {
    public ElementalsCapeFeatureRenderer(FeatureRendererContext<DecoyPlayerEntity, PlayerEntityModel<DecoyPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, DecoyPlayerEntity entity, float f, float g, float h, float j, float k, float l) {
        if(entity.getOwner() == null)
            return;
        if (!entity.isInvisible() && entity.getOwner().isPartVisible(PlayerModelPart.CAPE)) {
            SkinTextures skinTextures = ((AbstractClientPlayerEntity)entity.getOwner()).getSkinTextures();
            if (skinTextures.capeTexture() != null) {
                ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.CHEST);
                if (!itemStack.isOf(Items.ELYTRA)) {
                    matrixStack.push();
                    matrixStack.translate(0.0F, 0.0F, 0.125F);
                    double d = MathHelper.lerp((double)h, entity.prevCapeX, entity.capeX) - MathHelper.lerp((double)h, entity.prevX, entity.getX());
                    double e = MathHelper.lerp((double)h, entity.prevCapeY, entity.capeY) - MathHelper.lerp((double)h, entity.prevY, entity.getY());
                    double m = MathHelper.lerp((double)h, entity.prevCapeZ, entity.capeZ) - MathHelper.lerp((double)h, entity.prevZ, entity.getZ());
                    float n = MathHelper.lerpAngleDegrees(h, entity.prevBodyYaw, entity.bodyYaw);
                    double o = (double)MathHelper.sin(n * 0.017453292F);
                    double p = (double)(-MathHelper.cos(n * 0.017453292F));
                    float q = (float)e * 10.0F;
                    q = MathHelper.clamp(q, -6.0F, 32.0F);
                    float r = (float)(d * o + m * p) * 100.0F;
                    r = MathHelper.clamp(r, 0.0F, 150.0F);
                    float s = (float)(d * p - m * o) * 100.0F;
                    s = MathHelper.clamp(s, -20.0F, 20.0F);
                    if (r < 0.0F) {
                        r = 0.0F;
                    }


                    q += MathHelper.sin(MathHelper.lerp(h, entity.prevHorizontalSpeed, entity.horizontalSpeed) * 6.0F) * 32.0F * 0;
                    if (entity.isInSneakingPose()) {
                        q += 25.0F;
                    }

                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F + r / 2.0F + q));
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(s / 2.0F));
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - s / 2.0F));
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(skinTextures.capeTexture()));
                    this.getContextModel().renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
                    matrixStack.pop();
                }
            }
        }
    }
}

