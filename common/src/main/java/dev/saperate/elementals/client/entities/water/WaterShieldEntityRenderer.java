package dev.saperate.elementals.client.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.saperate.elementals.client.entities.utils.RenderUtils;
import dev.saperate.elementals.entities.water.WaterShieldEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

public class WaterShieldEntityRenderer extends EntityRenderer<WaterShieldEntity> {
    public static long firstTime = -1;
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");


    public WaterShieldEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WaterShieldEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 500;
        int color = BiomeColors.getAverageWaterColor(entity.level(),entity.getOnPos());

        matrices.pushPose();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());


//        drawCube(vertexConsumer, matrices, 255,
//                (color >> 16 & 255) / 255.0f,
//                (color >> 8 & 255) / 255.0f,
//                (color & 255) / 255.0f,
//                1,
//                texture,
//                1, new Matrix4f()
//                        .translate(0,3.5f, 0)
//                        .scale(4.7f)
//                        .rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot)))
//                        .rotate((float) Math.toRadians(90),1,0,0)
//                ,
//                true,
//                false,
//                false
//        );

        Matrix4f mat = new Matrix4f();
        mat.translate(0,0,0.5f);
        mat.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat.rotate(Axis.XP.rotationDegrees((float) Math.toDegrees(rot)));
        mat.translate(0,0,-0.5f);
        matrices.scale(2,2,2);
        matrices.translate(0,0.65f,-0.5f);

        RenderUtils.drawCube(vertexConsumer, matrices, 255,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.8f,
                texture,
                1, mat,
                true,
                true,
                true
        );


        Matrix4f mat2 = new Matrix4f();
        mat.scale(2f);
        mat2.translate(0,0,0.5f);
        mat2.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(-(rot + 2) * 2)));
        mat2.rotate(Axis.XP.rotationDegrees((float) Math.toDegrees(-(rot + 2))));
        mat2.translate(0,0,-0.5f);


        RenderUtils.drawCube(vertexConsumer, matrices, 255,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.8f,
                texture,
                1, mat2,
                true,
                true,
                true
        );



        RenderSystem.disableBlend();
        matrices.popPose();


    }



    @Override
    public ResourceLocation getTextureLocation(WaterShieldEntity entity) {
        return null;
    }
}
