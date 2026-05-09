package dev.saperate.elementals.client.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.air.AirBulletEntity;
import dev.saperate.elementals.entities.metal.MetalBulletEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.PoseStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class MetalBulletEntityRenderer extends EntityRenderer<MetalBulletEntity> {
    private static final Identifier texture = Identifier.of("minecraft", "block/iron_block");
    private static final Identifier topTexture = Identifier.of("elementals", "block/air_block_top");

    public MetalBulletEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(MetalBulletEntity entity, float yaw, float tickDelta, PoseStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 0.25f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        drawCube(vertexConsumer, matrices, light,
                .725f,
                .69f,
                .675f,
                1,
                texture,
                1,
                new Matrix4f().rotate((float) Math.toRadians(90),1,0,0).scale(0.15f),
                false,
                true,
                true
        );



        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(MetalBulletEntity entity) {
        return texture;
    }
}
