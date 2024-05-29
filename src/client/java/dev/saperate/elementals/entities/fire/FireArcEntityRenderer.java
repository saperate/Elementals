package dev.saperate.elementals.entities.fire;

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


public class FireArcEntityRenderer extends EntityRenderer<FireArcEntity> {
    private static final Identifier fireTex = new Identifier("minecraft", "block/fire_0");//"block/fire_0");
    private static final Identifier blueFireTex = new Identifier("minecraft", "block/soul_fire_0");//"block/fire_0");
    private static final Identifier fireCoreTex = new Identifier("minecraft", "block/shroomlight");
    private static final Identifier blueFireCoreTex = new Identifier("elementals", "block/bluefire_core");

    public FireArcEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FireArcEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        FireArcEntity child = entity.getChild();
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

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        int color = 0xFFFFFF;


        Vec3d dir = child.getPos().subtract(entity.getPos());
        float d = (float) dir.length() * 4;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));


        drawCube(vertexConsumer, matrices, 255,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                1,
                entity.isBlue() ? blueFireTex : fireTex,
                d, mat,
                false,
                false,
                false
        );

        matrices.scale(0.8f, 0.8f, 0.8f);

        int color2 = entity.isBlue() ? 0xffffff : 0xfff600;
        drawCube(vertexConsumer, matrices, 255,
                (color2 >> 16 & 255) / 255.0f,
                (color2 >> 8 & 255) / 255.0f,
                (color2 & 255) / 255.0f,
                0.5f,
                 entity.isBlue() ? blueFireCoreTex : fireCoreTex,
                d * 1.25f, mat,
                false,
                true,
                true
        );


        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(FireArcEntity entity) {
        return null;
    }
}
