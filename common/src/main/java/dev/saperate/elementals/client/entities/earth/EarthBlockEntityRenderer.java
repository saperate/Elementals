package dev.saperate.elementals.client.entities.earth;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.client.entities.models.earth.ShrapnelModel;
import dev.saperate.elementals.client.entities.models.earth.SpikeModel;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EarthBlockEntityRenderer extends EntityRenderer<EarthBlockEntity> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dirt.png");

    public EarthBlockEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EarthBlockEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.translate(-0.5f, 0, -0.5f);


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        switch (entity.getModelShapeId()) {
            case 1 -> {
                matrices.translate(0.5f, -1, 0.5f);
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.itemEntityTranslucentCull(getTextureLocation(entity)));

                Vec3 dir = entity.getDeltaMovement();
                matrices.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
                matrices.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));

                ShrapnelModel.getTexturedModelData().bakeRoot().render(
                        matrices, vertexConsumer, light, 0, 0xFFFFFFFF);
            }
            case 2 -> {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.itemEntityTranslucentCull(getTextureLocation(entity)));

                matrices.mulPose(Axis.XP.rotationDegrees(180));
                matrices.scale(2, 2, 2);
                matrices.translate(0.25f, -1.5f, -0.25f);

                SpikeModel.getTexturedModelData().bakeRoot().render(
                        matrices, vertexConsumer, light, 0, 0xFFFFFFFF);
            }
            default -> {
                BlockState state = entity.getBlockState();
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());
                Minecraft.getInstance().getBlockRenderer().renderBatched(state, entity.getOnPos(), entity.level(), matrices, vertexConsumer, false, entity.level().random);
            }


        }


        RenderSystem.disableBlend();
        matrices.popPose();
    }


    @Override
    public ResourceLocation getTextureLocation(EarthBlockEntity entity) {
        return texture;
    }
}
