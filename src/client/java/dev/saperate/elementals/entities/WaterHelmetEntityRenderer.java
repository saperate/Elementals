package dev.saperate.elementals.entities;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import dev.saperate.elementals.entities.water.WaterHelmetEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class WaterHelmetEntityRenderer extends EntityRenderer<WaterHelmetEntity> {
    private static final Identifier texture = new Identifier("minecraft", "block/water_flow");

    public WaterHelmetEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterHelmetEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Entity owner = entity.getOwner();
        entity.setPos(owner.getX(),owner.getEyeY() - 0.5f,owner.getZ());

        matrices.push();
        matrices.translate(0, 0.9f, 0);

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
                texture,
                0.9f,
                new Matrix4f().rotate((float) Math.toRadians(90),1,0,0),
                true,
                true,
                true
        );



        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(WaterHelmetEntity entity) {
        return texture;
    }
}
