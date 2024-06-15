package dev.saperate.elementals.entities.air;

import com.mojang.blaze3d.systems.RenderSystem;
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

public class AirShieldEntityRenderer extends EntityRenderer<AirShieldEntity> {
    public static long firstTime = -1;
    private static final Identifier texture = new Identifier("elementals", "block/air_block");
    private static final Identifier topTexture = new Identifier("elementals", "block/air_block_top");


    public AirShieldEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(AirShieldEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 500;

        matrices.push();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());


        Matrix4f mat = new Matrix4f();
        mat.translate(0,0,0.5f);
        mat.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot * 4)));
        mat.rotate(RotationAxis.POSITIVE_X.rotationDegrees(90));//(float) Math.toDegrees(rot)));
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
        mat2.rotate(RotationAxis.POSITIVE_X.rotationDegrees(0));//(float) Math.toDegrees(-(rot + 2) * 2)));
        mat2.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot * -2)));
        mat2.rotate(RotationAxis.POSITIVE_Z.rotationDegrees((float) Math.toDegrees(rot * 2)));
        //mat2.rotate(RotationAxis.POSITIVE_Z.rotationDegrees((float) Math.toDegrees(-(rot + 2))));

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
        matrices.pop();


    }



    @Override
    public Identifier getTexture(AirShieldEntity entity) {
        return null;
    }
}
