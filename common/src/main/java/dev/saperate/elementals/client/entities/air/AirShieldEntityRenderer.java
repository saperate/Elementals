package dev.saperate.elementals.client.entities.air;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.air.AirShieldEntity;
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

public class AirShieldEntityRenderer extends EntityRenderer<AirShieldEntity> {
    public static long firstTime = -1;
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block");
    private static final ResourceLocation topTexture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block_top");


    public AirShieldEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AirShieldEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 500;

        matrices.pushPose();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());


        Matrix4f mat = new Matrix4f();
        mat.translate(0,0,0.5f);
        mat.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot * 4)));
        mat.rotate(Axis.XP.rotationDegrees(90));//(float) Math.toDegrees(rot)));
        mat.translate(0,0,-0.5f);
        matrices.scale(2.5f,2.5f,2.5f);
        matrices.translate(0,0.65f,-0.5f);

        drawCube(vertexConsumer, matrices, 255,
                1,
                1,
                1,
                0.8f,
                texture,
                topTexture,
                1, mat,
                true,
                true,
                true
        );


        Matrix4f mat2 = new Matrix4f();
        mat2.translate(0,0,0.5f);
        mat2.rotate(Axis.XP.rotationDegrees(0));//(float) Math.toDegrees(-(rot + 2) * 2)));
        mat2.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot * -2)));
        mat2.rotate(Axis.ZP.rotationDegrees((float) Math.toDegrees(rot * 2)));
        //mat2.rotate(Axis.ZP.rotationDegrees((float) Math.toDegrees(-(rot + 2))));

        mat2.translate(0,0,-0.5f);


        drawCube(vertexConsumer, matrices, 255,
                1,
                1,
                1,
                0.8f,
                texture,
                1, mat2,
                true,
                false,
                false
        );



        RenderSystem.disableBlend();
        matrices.popPose();


    }



    @Override
    public ResourceLocation getTextureLocation(AirShieldEntity entity) {
        return null;
    }
}
