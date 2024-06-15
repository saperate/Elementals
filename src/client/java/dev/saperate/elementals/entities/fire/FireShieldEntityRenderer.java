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
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class FireShieldEntityRenderer extends EntityRenderer<FireShieldEntity> {

    public static float x = 0, y = 0, z = 0.5f;
    public static long firstTime = -1;

    private static final Identifier fireTex = new Identifier("minecraft", "block/fire_0");//"block/fire_0");
    private static final Identifier blueFireTex = new Identifier("minecraft", "block/soul_fire_0");//"block/fire_0");
    private static final Identifier fireCoreTex = new Identifier("minecraft", "block/shroomlight");
    private static final Identifier blueFireCoreTex = new Identifier("elementals", "block/bluefire_core");

    public FireShieldEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FireShieldEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 1000;

        matrices.push();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());



        drawCube(vertexConsumer, matrices, 255,
                (0xFFFFFF >> 16 & 255) / 255.0f,
                (0xFFFFFF >> 8 & 255) / 255.0f,
                (0xFFFFFF & 255) / 255.0f,
                0.9f,
                entity.isBlue() ? blueFireTex : fireTex,
                1, new Matrix4f()
                        .translate(0, entity.prevFlameSize, 0)
                        .scale(4.7f, entity.prevFlameSize, 4.7f)
                        .rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(-rot)))
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
                        .rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot)))
                        .rotate((float) Math.toRadians(90),1,0,0)
                ,
                true,
                false,
                false
        );

        Matrix4f mat = new Matrix4f();
        mat.translate(0,0,0.5f);
        mat.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rot)));
        mat.translate(0,0,-0.5f);
        matrices.scale(3,3,3);
        matrices.translate(0,0.5f,-0.5f);

        int color2 = entity.isBlue() ? 0xffffff : 0xfff600;
        drawCube(vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock()), matrices, 255,
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
        matrices.pop();


    }



    @Override
    public Identifier getTexture(FireShieldEntity entity) {
        return null;
    }
}
