package dev.saperate.elementals.entities.utils;

import net.minecraft.client.MinecraftClient;

import static dev.saperate.elementals.utils.SapsUtils.calculatePitch;
import static dev.saperate.elementals.utils.SapsUtils.calculateYaw;
import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.glRotatef;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.*;
import org.lwjgl.opengl.GL11;

import java.lang.Math;
import java.util.Vector;
import java.util.function.Function;

public abstract class RenderUtils {


    public static void drawCube(VertexConsumer vertexConsumer, MatrixStack matrices, int light,
                                float r, float g, float b, float a, Identifier tex, float height, Matrix4f rot) {

        Function<Identifier, Sprite> func = MinecraftClient.getInstance().getSpriteAtlas(new Identifier("minecraft", "textures/atlas/blocks.png"));


        Sprite sprite = func.apply(tex);
        float uMin = sprite.getMinU(), uMax = sprite.getMaxU();
        float vMin = sprite.getMinV(), vMax = sprite.getMaxV();
        float m = 0.5f;

        Vector4f v1 = new Vector4f(-m,m,height,1).mul(rot);
        Vector4f v2 = new Vector4f(-m,-m,height,1).mul(rot);
        Vector4f v3 = new Vector4f(m,-m,height,1).mul(rot);
        Vector4f v4 = new Vector4f(m,m,height,1).mul(rot);
        Vector4f v5 = new Vector4f(-m,-m,0,1).mul(rot);
        Vector4f v6 = new Vector4f(-m,m,0,1).mul(rot);
        Vector4f v7 = new Vector4f(m,m,0,1).mul(rot);
        Vector4f v8 = new Vector4f(m,-m,0,1).mul(rot);

        //Back face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                0, 0, 1,
                v5, v6, v7, v8
        );

        // Front face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                0, 0, 1,
                v1, v2, v3, v4
        );

        // Right face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v6, v5, v2, v1
        );


        // Left face
        drawQuad(vertexConsumer, matrices, light,
                uMin, uMax, vMin, vMax,
                r, g, b, a,
                -1, 0, 0,
                v8, v7, v4, v3
        );

        // Top face
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
                v2, v5, v8, v3
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

    public static void drawQuad(VertexConsumer vertexConsumer, MatrixStack matrices, int light,
                                float uMin, float uMax, float vMin, float vMax,
                                float r, float g, float b, float a,
                                float nx, float ny, float nz,
                                Vector4f vec1,
                                Vector4f vec2,
                                Vector4f vec3,
                                Vector4f vec4) {
        drawQuad(
                vertexConsumer,matrices,light,uMin,uMax,vMin,vMax,
                r,g,b,a,nx,ny,nz,
                vec1.x,vec1.y,vec1.z,
                vec2.x,vec2.y,vec2.z,
                vec3.x,vec3.y,vec3.z,
                vec4.x,vec4.y,vec4.z
        );
    }


}
