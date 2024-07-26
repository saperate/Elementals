package dev.saperate.elementals.entities.lightning;

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

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;


public class VoltArcEntityRenderer extends EntityRenderer<VoltArcEntity> {
    private static final Identifier fireTex = new Identifier(MODID, "block/lightning_block");//"block/fire_0");

    public VoltArcEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(VoltArcEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VoltArcEntity child = entity.getChild();
        if (child == null) {
            return;
        }

        matrices.push();
        matrices.scale(0.125f, 0.125f, 0.125f);
        //matrices.translate(0, 0.5f, 0);


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());


        Vec3d dir = child.getPos().subtract(entity.getPos());
        float d = (float) dir.length() * 8;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));

        for (int i = 1; i < 5; i++) {
            Matrix4f nMat = new Matrix4f(mat);
            nMat.scale((float)i / 4,(float)i / 4,(float)i / 4);
            drawCube(vertexConsumer, matrices, 255,
                    1,
                    1,
                    1,
                    (float)(4 / i) / 4,
                    fireTex,
                    d * (1 / ((float)i / 4)), nMat,
                    false,
                    true,
                    true
            );

        }



        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(VoltArcEntity entity) {
        return null;
    }
}
