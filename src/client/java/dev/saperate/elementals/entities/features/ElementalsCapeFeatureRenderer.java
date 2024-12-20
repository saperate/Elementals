package dev.saperate.elementals.entities.features;

import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ElementalsCapeFeatureRenderer extends FeatureRenderer<DecoyPlayerEntity, PlayerEntityModel<DecoyPlayerEntity>> {
    public ElementalsCapeFeatureRenderer(FeatureRendererContext<DecoyPlayerEntity, PlayerEntityModel<DecoyPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, DecoyPlayerEntity entity, float f, float g, float h, float j, float k, float l) {

        if (entity.isInvisible()) {
            return;
        }
        return;//TODO add this back so decoys have capes and elytras
        /*Identifier skinTextures = ((AbstractClientPlayerEntity) entity.getOwner()).getCapeTexture()
        if (skinTextures.capeTexture() == null) {
            return;
        }
        ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.isOf(Items.ELYTRA)) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0f, 0.0f, 0.125f);
        double d = MathHelper.lerp((double)h, (double)entity.prevCapeX, (double)entity.capeX) - MathHelper.lerp((double)h, (double)entity.prevX, (double)entity.getX());
        double e = MathHelper.lerp((double)h, (double)entity.prevCapeY, (double)entity.capeY) - MathHelper.lerp((double)h, (double)entity.prevY, (double)entity.getY());
        double m = MathHelper.lerp((double)h, (double)entity.prevCapeZ, (double)entity.capeZ) - MathHelper.lerp((double)h, (double)entity.prevZ, (double)entity.getZ());
        float n = MathHelper.lerpAngleDegrees((float)h, (float)entity.prevBodyYaw, (float)entity.bodyYaw);
        double o = MathHelper.sin((float)(n * ((float)Math.PI / 180)));
        double p = -MathHelper.cos((float)(n * ((float)Math.PI / 180)));
        float q = (float)e * 10.0f;
        q = MathHelper.clamp((float)q, (float)-6.0f, (float)32.0f);
        float r = (float)(d * o + m * p) * 100.0f;
        r = MathHelper.clamp((float)r, (float)0.0f, (float)150.0f);
        float s = (float)(d * p - m * o) * 100.0f;
        s = MathHelper.clamp((float)s, (float)-20.0f, (float)20.0f);
        if (r < 0.0f) {
            r = 0.0f;
        }
        float t = 0;
        q += MathHelper.sin((float)(MathHelper.lerp((float)h, (float)entity.prevHorizontalSpeed, (float)entity.horizontalSpeed) * 6.0f)) * 32.0f * t;
        if (entity.isInSneakingPose()) {
            q += 25.0f;
        }
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0f + r / 2.0f + q));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(s / 2.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - s / 2.0f));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(skinTextures.capeTexture()));
        ((PlayerEntityModel)this.getContextModel()).renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        */
    }
}

