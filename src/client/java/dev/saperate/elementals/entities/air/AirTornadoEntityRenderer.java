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

public class AirTornadoEntityRenderer extends EntityRenderer<AirTornadoEntity> {
    private static final Identifier texture = new Identifier("elementals", "block/air_block");
    private static final Identifier topTexture = new Identifier("elementals", "block/air_block_top");
    public static long firstTime = -1;

    public AirTornadoEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(AirTornadoEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (firstTime == -1) {
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 1000;

        matrices.push();
        matrices.translate(0, 1f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        int tornadoSize = 4;
        for (int i = 0; i < tornadoSize; i++) {
            float s = 0.5f * i + 1;

            drawCube(vertexConsumer, matrices, light,
                    1,
                    1,
                    1,
                    1,
                    texture,
                    topTexture,
                    1,
                    new Matrix4f().rotate((float) Math.toRadians(90), 1, 0, 0)
                            .translate(0, 0, -i)//For some weird ass reason -z is the +y in game with these
                            .rotate(RotationAxis.POSITIVE_Z.rotationDegrees(
                                    (float) Math.toDegrees(rot
                                            * (i % 2 == 0 ? -5.5 : 5)
                                            + 1 - ((double) i / tornadoSize))
                            ))
                            .scale(s, s, 1),
                    true,
                    true,
                    true
            );
        }


        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(AirTornadoEntity entity) {
        return texture;
    }
}
