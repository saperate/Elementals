package dev.saperate.elementals.client.entities.air;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.air.AirTornadoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

public class AirTornadoEntityRenderer extends EntityRenderer<AirTornadoEntity> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block");
    private static final ResourceLocation topTexture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block_top");
    public static long firstTime = -1;

    public AirTornadoEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AirTornadoEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (firstTime == -1) {
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 1000;

        matrices.pushPose();
        matrices.translate(0, 1f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());

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
                            .rotate(Axis.ZP.rotationDegrees(
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
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AirTornadoEntity entity) {
        return texture;
    }
}
