package dev.saperate.elementals.entities.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class RenderUtils {
    public static void drawCube(VertexConsumer vertexConsumer, MatrixStack matrices, int light,
                                float r, float g, float b, float a, Identifier tex) {

        Function<Identifier, Sprite> func = MinecraftClient.getInstance().getSpriteAtlas(new Identifier("minecraft", "textures/atlas/blocks.png"));

        //func.apply(tex);

        Sprite sprite = func.apply(tex);
        float uMin = sprite.getMinU(), uMax = sprite.getMaxU();
        float vMin = sprite.getMinV(), vMax = sprite.getMaxV();
        float m = 0.5f;

        //Back face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                0, 0, 1,
                -m, -m, -m,
                -m, m, -m,
                m, m, -m,
                m, -m, -m
        );

        // Front face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                0, 0, 1,
                -m, m, m,
                -m, -m, m,
                m, -m, m,
                m, m, m
        );

        // Right face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                -m, m, -m,
                -m, -m, -m,
                -m, -m, m,
                -m, m, m
        );


        // Left face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                m, -m, -m,
                m, m, -m,
                m, m, m,
                m, -m, m
        );

        // Top face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                0, 1, 0,
                -m, m, -m,
                -m, m, m,
                m, m, m,
                m, m, -m
        );


        // Bottom face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                -m, -m, m,
                -m, -m, -m,
                m, -m, -m,
                m, -m, m
        );

    }

    public static void drawQuad(VertexConsumer vertexConsumer, MatrixStack matrices, int light,
                                float uMin, float uMax, float vMin, float vMax,
                                float r, float g, float b, float a,
                                float nx, float ny, float nz,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4) {
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x1, y1, z1).color(r, g, b, a).texture(uMin, vMin).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x2, y2, z2).color(r, g, b, a).texture(uMin, vMax).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x3, y3, z3).color(r, g, b, a).texture(uMax, vMax).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x4, y4, z4).color(r, g, b, a).texture(uMax, vMin).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
    }
}
