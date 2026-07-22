package dev.saperate.elementals.client.entities.air;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.client.entities.models.air.AirBladeModel;
import dev.saperate.elementals.entities.air.AirBladeEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static dev.saperate.elementals.client.ElementalsClient.MODEL_AIR_BLADE_LAYER;

public class AirBladeEntityRenderer extends EntityRenderer<AirBladeEntity> implements RenderLayerParent<AirBladeEntity, AirBladeModel> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/entity/air_blade.png");
    private final AirBladeModel model;

    public AirBladeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new AirBladeModel(context.bakeLayer(MODEL_AIR_BLADE_LAYER));
    }

    @Override
    public void render(AirBladeEntity entity, float entityYaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        //orient the blade along its actual flight direction so it visually "points" and slices
        //forward instead of just spinning in place
        float yaw = Mth.rotLerp(tickDelta, entity.yRotO, entity.getYRot());
        float pitch = Mth.rotLerp(tickDelta, entity.xRotO, entity.getXRot());
        matrices.mulPose(Axis.YP.rotationDegrees(-yaw));
        matrices.mulPose(Axis.XP.rotationDegrees(pitch));

        //small continuous wobble around its own axis so the flat blade still catches the light
        //and reads as three-dimensional from every angle, like a spinning air-cut sliver
        float wobble = (entity.tickCount + tickDelta) * 14f;
        matrices.mulPose(Axis.ZP.rotationDegrees(wobble));

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.itemEntityTranslucentCull(getTextureLocation(entity)));

        AirBladeModel.getTexturedModelData().bakeRoot().render(
                matrices, vertexConsumer, light, 0,
                0xCCFFFFFF
        );

        RenderSystem.disableBlend();
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AirBladeEntity entity) {
        return texture;
    }

    @Override
    public AirBladeModel getModel() {
        return this.model;
    }
}