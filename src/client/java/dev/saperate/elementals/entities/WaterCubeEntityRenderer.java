package dev.saperate.elementals.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class WaterCubeEntityRenderer extends EntityRenderer<WaterCubeEntity> {
    private static final Identifier texture = new Identifier("minecraft", "block/water_still");

    public WaterCubeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterCubeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(1, 1, 1);
        matrices.translate(0, 0.5f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getSolid());

        drawCube(vertexConsumer, matrices, light,
                1,1,1,
                texture
        );

        matrices.pop();
    }

    public static void drawCube(VertexConsumer vertexConsumer, MatrixStack matrices, int light,
                         float r, float g, float b, Identifier tex) {

        Function<Identifier, Sprite> func = MinecraftClient.getInstance().getSpriteAtlas(new Identifier("minecraft","textures/atlas/blocks.png"));

        //func.apply(tex);

        Sprite sprite = func.apply(tex);
        float uvMin = sprite.getMinU(), uvMax = sprite.getMaxU();
        System.out.println(sprite);
        float m = 0.5f;

        //Back face
        drawQuad(vertexConsumer, matrices, light,
                uvMin,uvMax,
                r,g,b,
                0, 0, 1,
                -m, -m, -m,
                -m, m, -m,
                m, m, -m,
                m, -m, -m
        );

        // Front face
        drawQuad(vertexConsumer, matrices, light,
                uvMin,uvMax,
                r,g,b,
                0, 0, 1,
                -m, m, m,
                -m, -m, m,
                m, -m, m,
                m, m, m
        );

        // Right face
        drawQuad(vertexConsumer, matrices, light,
                uvMin,uvMax,
                r,g,b,
                -1, 0, 0,
                -m, m, -m,
                -m, -m, -m,
                -m, -m, m,
                -m, m, m
        );


        // Left face
        drawQuad(vertexConsumer, matrices, light,
                uvMin,uvMax,
                r,g,b,
                -1, 0, 0,
                m, -m, -m,
                m, m, -m,
                m, m, m,
                m, -m, m
        );

        // Top face
        drawQuad(vertexConsumer, matrices, light,
                uvMin,uvMax,
                r,g,b,
                0, 1, 0,
                -m, m, -m,
                -m, m, m,
                m, m, m,
                m, m, -m
        );


        // Bottom face
        drawQuad(vertexConsumer, matrices, light,
                uvMin,uvMax,
                r,g,b,
                -1, 0, 0,
                -m, -m, m,
                -m, -m, -m,
                m, -m, -m,
                m, -m, m
        );

    }

    public static void drawQuad(VertexConsumer vertexConsumer, MatrixStack matrices, int light,
                         float uvMin, float uvMax,
                         float r, float g, float b,
                         float nx, float ny, float nz,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4) {
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x1, y1, z1).color(r, g, b, 1.0f).texture(uvMin, uvMin).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x2, y2, z2).color(r, g, b, 1.0f).texture(uvMin, uvMax).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x3, y3, z3).color(r, g, b, 1.0f).texture(uvMax, uvMax).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x4, y4, z4).color(r, g, b, 1.0f).texture(uvMax, uvMin).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
    }

    @Override
    public Identifier getTexture(WaterCubeEntity entity) {
        return texture;
    }
}
