package dev.saperate.elementals.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.utils.RenderUtils;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class WaterShieldEntityRenderer extends EntityRenderer<WaterShieldEntity> {
    public static long firstTime = -1;
    private static final Identifier texture = new Identifier("minecraft", "block/water_flow");


    public WaterShieldEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterShieldEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 500;
        int color = BiomeColors.getWaterColor(entity.getWorld(),entity.getBlockPos());

        matrices.push();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());


//        drawCube(vertexConsumer, matrices, 255,
//                (color >> 16 & 255) / 255.0f,
//                (color >> 8 & 255) / 255.0f,
//                (color & 255) / 255.0f,
//                1,
//                texture,
//                1, new Matrix4f()
//                        .translate(0,3.5f, 0)
//                        .scale(4.7f)
//                        .rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot)))
//                        .rotate((float) Math.toRadians(90),1,0,0)
//                ,
//                true,
//                false,
//                false
//        );

        Matrix4f mat = new Matrix4f();
        mat.translate(0,0,0.5f);
        mat.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rot)));
        mat.translate(0,0,-0.5f);
        matrices.scale(2,2,2);
        matrices.translate(0,0.65f,-0.5f);

        RenderUtils.drawCube(vertexConsumer, matrices, 255,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                1,
                texture,
                1, mat,
                true,
                true,
                true
        );


        Matrix4f mat2 = new Matrix4f();
        mat.scale(2f);
        mat2.translate(0,0,0.5f);
        mat2.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(-(rot + 2) * 2)));
        mat2.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(-(rot + 2))));
        mat2.translate(0,0,-0.5f);


        RenderUtils.drawCube(vertexConsumer, matrices, 255,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                1,
                texture,
                1, mat2,
                true,
                true,
                true
        );



        RenderSystem.disableBlend();
        matrices.pop();


    }



    @Override
    public Identifier getTexture(WaterShieldEntity entity) {
        return null;
    }
}
