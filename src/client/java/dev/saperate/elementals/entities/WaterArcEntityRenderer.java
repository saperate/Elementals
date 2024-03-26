package dev.saperate.elementals.entities;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;
import static dev.saperate.elementals.entities.utils.RenderUtils.drawSegment;


public class WaterArcEntityRenderer extends EntityRenderer<WaterArcEntity> {
    private static final Identifier texture = new Identifier("minecraft", "block/water_still");

    public WaterArcEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterArcEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(1, 1f, 1);
        matrices.translate(0, 0.5f, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        int color = BiomeColors.getWaterColor(entity.getWorld(),entity.getBlockPos());


        drawCube(vertexConsumer, matrices, light,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.9f,
                texture

        );

        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(WaterArcEntity entity) {
        return texture;
    }
}
