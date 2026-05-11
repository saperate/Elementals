package dev.saperate.elementals.client.entities.fire;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

public class FireShieldEntityRenderer extends EntityRenderer<FireShieldEntity> {

    public static float x = 0, y = 0, z = 0.5f;
    public static long firstTime = -1;

    private static final ResourceLocation fireTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/fire_0");//"block/fire_0");
    private static final ResourceLocation blueFireTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_fire_0");//"block/fire_0");
    private static final ResourceLocation fireCoreTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/shroomlight");
    private static final ResourceLocation blueFireCoreTex = ResourceLocation.fromNamespaceAndPath("elementals", "block/bluefire_core");

    public FireShieldEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FireShieldEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 1000;

        matrices.pushPose();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.cutout());



        drawCube(vertexConsumer, matrices, 255,
                (0xFFFFFF >> 16 & 255) / 255.0f,
                (0xFFFFFF >> 8 & 255) / 255.0f,
                (0xFFFFFF & 255) / 255.0f,
                0.9f,
                entity.isBlue() ? blueFireTex : fireTex,
                1, new Matrix4f()
                        .translate(0, entity.prevFlameSize, 0)
                        .scale(4.7f, entity.prevFlameSize, 4.7f)
                        .rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(-rot)))
                        .rotate((float) Math.toRadians(90),1,0,0)
                ,
                true,
                false,
                false
        );

        drawCube(vertexConsumer, matrices, 255,
                (0xFFFFFF >> 16 & 255) / 255.0f,
                (0xFFFFFF >> 8 & 255) / 255.0f,
                (0xFFFFFF & 255) / 255.0f,
                0.9f,
                entity.isBlue() ? blueFireTex : fireTex,
                1, new Matrix4f()
                        .translate(0, entity.prevFlameSize, 0)
                        .scale(4.7f, entity.prevFlameSize, 4.7f)
                        .rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot)))
                        .rotate((float) Math.toRadians(90),1,0,0)
                ,
                true,
                false,
                false
        );

        Matrix4f mat = new Matrix4f();
        mat.translate(0,0,0.5f);
        mat.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat.rotate(Axis.XP.rotationDegrees((float) Math.toDegrees(rot)));
        mat.translate(0,0,-0.5f);
        matrices.scale(3,3,3);
        matrices.translate(0,0.5f,-0.5f);

        int color2 = entity.isBlue() ? 0xffffff : 0xfff600;
        drawCube(vertexConsumers.getBuffer(RenderType.translucentMovingBlock()), matrices, 255,
                (color2 >> 16 & 255) / 255.0f,
                (color2 >> 8 & 255) / 255.0f,
                (color2 & 255) / 255.0f,
                0.65f,
                entity.isBlue() ? blueFireCoreTex : fireCoreTex,
                1, mat,
                true,
                true,
                true
        );

        RenderSystem.disableBlend();
        matrices.popPose();


    }



    @Override
    public ResourceLocation getTextureLocation(FireShieldEntity entity) {
        return fireTex;
    }
}
