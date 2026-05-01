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
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;


public class AirStreamEntityRenderer extends EntityRenderer<AirStreamEntity> {
    private static final Identifier texture = Identifier.of("elementals", "block/air_block");
    private static final Identifier topTexture = Identifier.of("elementals", "block/air_block_top");

    public AirStreamEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(AirStreamEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        AirStreamEntity child = entity.getChild();
        if (child == null) {
            return;
        }

        matrices.push();
        matrices.scale(0.25f, 0.25f, 0.25f);
        //matrices.translate(0, 0.5f, 0);


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        Vec3d dir = child.getPos().subtract(entity.getPos());
        float d = (float) dir.length() * 4;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));


        drawCube(vertexConsumer, matrices, 255,
                1,
                1,
                1,
                1,
                texture,
                topTexture,
                d, mat,
                false,
                true,
                true
        );

        matrices.scale(0.8f, 0.8f, 0.8f);
        drawCube(vertexConsumer, matrices, 255,
                1,
                1,
                1,
                1,
                texture,
                topTexture,
                d, mat,
                false,
                true,
                true
        );


        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(AirStreamEntity entity) {
        return null;
    }
}
