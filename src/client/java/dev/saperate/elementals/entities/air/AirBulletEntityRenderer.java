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
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class AirBulletEntityRenderer extends EntityRenderer<AirBulletEntity> {
    private static final Identifier texture = new Identifier("elementals", "block/air_block");
    private static final Identifier topTexture = new Identifier("elementals", "block/air_block_top");

    public AirBulletEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(AirBulletEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 0.25f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        drawCube(vertexConsumer, matrices, light,
                1,
                1,
                1,
                1,
                texture,
                topTexture,
                1,
                new Matrix4f().rotate((float) Math.toRadians(90),1,0,0).scale(0.25f),
                false,
                true,
                true
        );



        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(AirBulletEntity entity) {
        return texture;
    }
}
