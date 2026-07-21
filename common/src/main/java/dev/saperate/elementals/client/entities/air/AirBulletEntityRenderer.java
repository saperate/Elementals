package dev.saperate.elementals.client.entities.air;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saperate.elementals.entities.air.AirBulletEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

public class AirBulletEntityRenderer extends EntityRenderer<AirBulletEntity> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block");
    private static final ResourceLocation topTexture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block_top");

    public AirBulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AirBulletEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.translate(0, 0.25f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());

        drawCube(vertexConsumer, matrices, light,
                1,
                1,
                1,
                1,
                texture,
                topTexture,
                1,
                new Matrix4f().rotate((float) Math.toRadians(90),1,0,0).scale(0.25f),
                false,
                true,
                true
        );



        RenderSystem.disableBlend();
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AirBulletEntity entity) {
        return texture;
    }
}