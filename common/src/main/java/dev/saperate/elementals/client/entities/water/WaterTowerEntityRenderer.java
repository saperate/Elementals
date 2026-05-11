package dev.saperate.elementals.client.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.water.WaterTowerEntity;
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

public class WaterTowerEntityRenderer extends EntityRenderer<WaterTowerEntity> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");
    public static long firstTime = -1;

    public WaterTowerEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WaterTowerEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {

        entity.updatePosition(entity.getOwner());

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

        int color = BiomeColors.getAverageWaterColor(entity.level(),entity.getOnPos());


        for (int i = 0; i < Math.floor(1 + entity.getTowerHeight()); i++) {

            float s = 1 * Math.max(0.5f, (1.5f - (i / entity.getTowerHeight())));

            drawCube(vertexConsumer, matrices, light,
                    (color >> 16 & 255) / 255.0f,
                    (color >> 8 & 255) / 255.0f,
                    (color & 255) / 255.0f,
                    0.9f,
                    texture,
                    1,
                    new Matrix4f().rotate((float) Math.toRadians(90),1,0,0)
                            .translate(0,0,-i)//For some weird ass reason -z is the +y in game with these
                            .rotate(Axis.ZP.rotationDegrees(
                                    (float) Math.toDegrees(rot
                                            * (i % 2 == 0 ? -5.5 : 5)
                                            + 1 - ((double) i / entity.getMaxTowerHeight()))
                                    ))
                            .scale(s,s,1),
                    false,
                    true,
                    true
            );
        }




        RenderSystem.disableBlend();
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(WaterTowerEntity entity) {
        return texture;
    }
}
