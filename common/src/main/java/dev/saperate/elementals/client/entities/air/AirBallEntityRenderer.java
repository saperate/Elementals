package dev.saperate.elementals.client.entities.air;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.air.AirBallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

public class AirBallEntityRenderer extends EntityRenderer<AirBallEntity> {
    public static long firstTime = -1;

    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block");
    private static final ResourceLocation topTexture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block_top");

    public AirBallEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AirBallEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 500;

        matrices.pushPose();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());

        matrices.translate(-0.5f, 0, -0.5f);

        Matrix4f mat = new Matrix4f();
        matrices.translate(0.5f,.5f,0);
        mat.translate(0,0,.5f);
        mat.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot)));
        mat.rotate(Axis.ZP.rotationDegrees((float) Math.toDegrees(rot)));
        mat.translate(0,0,-.5f);
        drawCube(vertexConsumer, matrices, 255,
                1,
                1,
                1,
                1,
                texture,
                topTexture,
                1, mat,
                false,
                true,
                true
        );

        Matrix4f mat2 = new Matrix4f();
        mat2.translate(0,0,.5f);
        mat2.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat2.rotate(Axis.XP.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat2.translate(0,0,-.5f);

        matrices.scale(.9f,.9f,.9f);


        drawCube(vertexConsumer, matrices, 255,
                1,
                1,
                1,
                1,
                texture,
                topTexture,
                1, mat2,
                false,
                true,
                true
        );

        RenderSystem.disableBlend();
        matrices.popPose();
    }


    @Override
    public ResourceLocation getTextureLocation(AirBallEntity entity) {
        return null;
    }
}
