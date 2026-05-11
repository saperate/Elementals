package dev.saperate.elementals.client.entities.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;
import java.util.function.Function;

public abstract class RenderUtils {

    public static void drawCube(VertexConsumer vertexConsumer, PoseStack matrices, int light,
                                float r, float g, float b, float a, ResourceLocation tex, ResourceLocation topTex, float height, Matrix4f rot,
                                boolean doubleSided, boolean renderTop, boolean renderBottom) {

        Function<ResourceLocation, TextureAtlasSprite> func = Minecraft.getInstance()
                .getTextureAtlas(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/atlas/blocks.png"));


        TextureAtlasSprite sprite = func.apply(tex);
        float uMin = sprite.getU0(), uMax = sprite.getU1();
        float vMin = sprite.getV0(), vMax = sprite.getV1();
        
        float m = 0.5f;
        Vector4f v1 = new Vector4f(-m, m, height, 1).mul(rot);
        Vector4f v2 = new Vector4f(-m, -m, height, 1).mul(rot);
        Vector4f v3 = new Vector4f(m, -m, height, 1).mul(rot);
        Vector4f v4 = new Vector4f(m, m, height, 1).mul(rot);
        Vector4f v5 = new Vector4f(-m, -m, 0, 1).mul(rot);
        Vector4f v6 = new Vector4f(-m, m, 0, 1).mul(rot);
        Vector4f v7 = new Vector4f(m, m, 0, 1).mul(rot);
        Vector4f v8 = new Vector4f(m, -m, 0, 1).mul(rot);


        // Right face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v5, v2, v1, v6
        );


        // Left face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v7, v4, v3, v8
        );


        // front face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                0, 1, 0,
                v6, v1, v4, v7
        );


        // Bottom face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v8, v3, v2, v5
        );

        if (tex != topTex) {
            TextureAtlasSprite topSprite = func.apply(topTex);
            uMin = topSprite.getU0();
            uMax = topSprite.getU1();
            vMin = topSprite.getV0();
            vMax = topSprite.getV1();
        }

        if (renderTop) {
            // Top face
            drawQuad(vertexConsumer, matrices, light,
                    uMin, uMax, vMin, vMax,
                    r, g, b, a,
                    0, 0, 1,
                    v5, v6, v7, v8
            );
        }

        if (renderBottom) {
            // Bottom face
            drawQuad(vertexConsumer, matrices, light,
                    uMin, uMax, vMin, vMax,
                    r, g, b, a,
                    0, 0, 1,
                    v1, v2, v3, v4
            );
        }


        if (doubleSided) {
            drawInvertedCube(vertexConsumer, matrices, light, r, g, b, a, tex, topTex,height, rot, renderTop, renderBottom);
        }
    }

    public static void drawCube(VertexConsumer vertexConsumer, PoseStack matrices, int light,
                                float r, float g, float b, float a, ResourceLocation tex, float height, Matrix4f rot,
                                boolean doubleSided, boolean renderTop, boolean renderBottom) {
        drawCube(vertexConsumer, matrices, light, r, g, b, a, tex, tex, height, rot, doubleSided, renderTop, renderBottom);
    }

    public static void drawInvertedCube(VertexConsumer vertexConsumer, PoseStack matrices, int light,
                                        float r, float g, float b, float a, ResourceLocation tex, ResourceLocation topTex, float height, Matrix4f rot,
                                        boolean renderTop, boolean renderBottom) {


        Function<ResourceLocation, TextureAtlasSprite> func = Minecraft.getInstance().getTextureAtlas(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/atlas/blocks.png"));

        TextureAtlasSprite sprite = func.apply(tex);
        float uMin = sprite.getU0(), uMax = sprite.getU1();
        float vMin = sprite.getV0(), vMax = sprite.getV1();
        float m = 0.5f;

        Vector4f v1 = new Vector4f(-m, m, height, 1).mul(rot);
        Vector4f v2 = new Vector4f(-m, -m, height, 1).mul(rot);
        Vector4f v3 = new Vector4f(m, -m, height, 1).mul(rot);
        Vector4f v4 = new Vector4f(m, m, height, 1).mul(rot);
        Vector4f v5 = new Vector4f(-m, -m, 0, 1).mul(rot);
        Vector4f v6 = new Vector4f(-m, m, 0, 1).mul(rot);
        Vector4f v7 = new Vector4f(m, m, 0, 1).mul(rot);
        Vector4f v8 = new Vector4f(m, -m, 0, 1).mul(rot);


        // Right face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v6, v1, v2, v5
        );

        // Left face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v8, v3, v4, v7
        );

        // Front face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                0, 1, 0,
                v7, v4, v1, v6
        );


        // Back face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v5, v2, v3, v8
        );


        if (tex != topTex) {
            TextureAtlasSprite topSprite = func.apply(topTex);
            uMin = topSprite.getU0();
            uMax = topSprite.getU1();
            vMin = topSprite.getV0();
            vMax = topSprite.getV1();
        }

        if (renderTop) {
            // Top face
            drawQuad(vertexConsumer, matrices, light,
                    uMin, uMax, vMin, vMax,
                    r, g, b, a,
                    0, 0, 1,
                    v6, v5, v8, v7
            );
        }

        if (renderBottom) {
            // Bottom face
            drawQuad(vertexConsumer, matrices, light,
                    uMin, uMax, vMin, vMax,
                    r, g, b, a,
                    0, 0, 1,
                    v2, v1, v4, v3
            );
        }
    }


    public static void drawQuad(VertexConsumer vertexConsumer, PoseStack matrices, int light,
                                float uMin, float uMax, float vMin, float vMax,
                                float r, float g, float b, float a,
                                float nx, float ny, float nz,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4) {

        vertexConsumer.addVertex(matrices.last().pose(), x1, y1, z1).setColor(r, g, b, a).setUv(uMin, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vertexConsumer.addVertex(matrices.last().pose(), x2, y2, z2).setColor(r, g, b, a).setUv(uMin, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vertexConsumer.addVertex(matrices.last().pose(), x3, y3, z3).setColor(r, g, b, a).setUv(uMax, vMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vertexConsumer.addVertex(matrices.last().pose(), x4, y4, z4).setColor(r, g, b, a).setUv(uMax, vMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
    }

    public static void drawQuad(VertexConsumer vertexConsumer, PoseStack matrices, int light,
                                float uMin, float uMax, float vMin, float vMax,
                                float r, float g, float b, float a,
                                float nx, float ny, float nz,
                                Vector4f vec1,
                                Vector4f vec2,
                                Vector4f vec3,
                                Vector4f vec4) {
        drawQuad(
                vertexConsumer, matrices, light, uMin, uMax, vMin, vMax,
                r, g, b, a, nx, ny, nz,
                vec1.x, vec1.y, vec1.z,
                vec2.x, vec2.y, vec2.z,
                vec3.x, vec3.y, vec3.z,
                vec4.x, vec4.y, vec4.z
        );
    }


}
