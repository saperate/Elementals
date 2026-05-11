package dev.saperate.elementals.client.entities.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElementalsCapeFeatureRenderer extends RenderLayer<DecoyPlayerEntity, PlayerModel<DecoyPlayerEntity>> {
    public ElementalsCapeFeatureRenderer(RenderLayerParent<DecoyPlayerEntity, PlayerModel<DecoyPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, DecoyPlayerEntity entity, float f, float g, float h, float j, float k, float l) {
        if(entity.getOwner() == null)
            return;
        if (!entity.isInvisible() && entity.getOwner().isModelPartShown(PlayerModelPart.CAPE)) {
            PlayerSkin skinTextures = ((AbstractClientPlayer)entity.getOwner()).getSkin();
            if (skinTextures.capeTexture() != null) {
                ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemStack.is(Items.ELYTRA)) {
                    matrixStack.pushPose();
                    matrixStack.translate(0.0F, 0.0F, 0.125F);
                    double d = Mth.lerp((double)h, entity.prevCapeX, entity.capeX) - Mth.lerp((double)h, entity.xo, entity.getX());
                    double e = Mth.lerp((double)h, entity.prevCapeY, entity.capeY) - Mth.lerp((double)h, entity.yo, entity.getY());
                    double m = Mth.lerp((double)h, entity.prevCapeZ, entity.capeZ) - Mth.lerp((double)h, entity.zo, entity.getZ());
                    float n = Mth.rotLerp(h, entity.yBodyRotO, entity.yBodyRot);
                    double o = (double) Mth.sin(n * 0.017453292F);
                    double p = (double)(-Mth.cos(n * 0.017453292F));
                    float q = (float)e * 10.0F;
                    q = Mth.clamp(q, -6.0F, 32.0F);
                    float r = (float)(d * o + m * p) * 100.0F;
                    r = Mth.clamp(r, 0.0F, 150.0F);
                    float s = (float)(d * p - m * o) * 100.0F;
                    s = Mth.clamp(s, -20.0F, 20.0F);
                    if (r < 0.0F) {
                        r = 0.0F;
                    }


                    q += Mth.sin((float) entity.getDeltaMovement().multiply(1,0,1).length() * 6.0F) * 32.0F * 0;
                    if (entity.isCrouching()) {
                        q += 25.0F;
                    }

                    matrixStack.mulPose(Axis.XP.rotationDegrees(6.0F + r / 2.0F + q));
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(s / 2.0F));
                    matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - s / 2.0F));
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entitySolid(skinTextures.capeTexture()));
                    this.getParentModel().renderCloak(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
                    matrixStack.popPose();
                }
            }
        }
    }
}

