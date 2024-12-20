package dev.saperate.elementals.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
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

public class WaterTowerEntityRenderer extends EntityRenderer<WaterTowerEntity> {
    private static final Identifier texture = Identifier.of("minecraft", "block/water_flow");
    public static long firstTime = -1;

    public WaterTowerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterTowerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        entity.updatePosition(entity.getOwner());

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

        int color = BiomeColors.getWaterColor(entity.getWorld(),entity.getBlockPos());


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
                            .rotate(RotationAxis.POSITIVE_Z.rotationDegrees(
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
        matrices.pop();
    }

    @Override
    public Identifier getTexture(WaterTowerEntity entity) {
        return texture;
    }
}
