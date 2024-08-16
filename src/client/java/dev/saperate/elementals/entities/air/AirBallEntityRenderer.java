package dev.saperate.elementals.entities.air;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class AirBallEntityRenderer extends EntityRenderer<AirBallEntity> {
    public static long firstTime = -1;

    private static final Identifier texture = new Identifier("elementals", "block/air_block");
    private static final Identifier topTexture = new Identifier("elementals", "block/air_block_top");

    public AirBallEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(AirBallEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 500;

        matrices.push();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        matrices.translate(-0.5f, 0, -0.5f);

        Matrix4f mat = new Matrix4f();
        matrices.translate(0.5f,.5f,0);
        mat.translate(0,0,.5f);
        mat.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot)));
        mat.rotate(RotationAxis.POSITIVE_Z.rotationDegrees((float) Math.toDegrees(rot)));
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
        mat2.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat2.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rot * 2)));
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
        matrices.pop();
    }


    @Override
    public Identifier getTexture(AirBallEntity entity) {
        return null;
    }
}
