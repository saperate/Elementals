package dev.saperate.elementals.entities.blood;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;


public class BloodShotEntityRenderer extends EntityRenderer<BloodShotEntity> {
    private static final Identifier texture = new Identifier("minecraft", "block/water_still");

    public BloodShotEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(BloodShotEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        int color = 0xf50f00;




        drawCube(vertexConsumer, matrices, light,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.9f,
                texture,
                0.9f,
                new Matrix4f().rotate((float) Math.toRadians(90),1,0,0)
                        .translate(0,0,-0.125f)
                        .scale(0.25f,0.25f,0.25f),
                false,
                true,
                true
        );


    }

    @Override
    public Identifier getTexture(BloodShotEntity entity) {
        return texture;
    }
}
