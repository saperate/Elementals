package dev.saperate.elementals.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;


public class MetalCableEntityRenderer extends EntityRenderer<MetalCableEntity> {
    private static final Identifier fireTex = Identifier.of("minecraft", "block/iron_block");//"block/fire_0");

    public MetalCableEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(MetalCableEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.getChild() == null) {
            return;
        }

        if (entity.getParent() == null) {//todo figure out why its so jittery, 99% sure its cause of some fuckery like this
            entity.setPosition(entity.getOwner().getLeashPos(tickDelta));
        }

        Entity pointA = entity.getChild();
        Entity pointB = entity.getOwner();


        float distance = pointA.distanceTo(pointB);

        matrices.push();
        Vec3d vec3d = pointA.getLeashPos(tickDelta);
        double d = (double) (MathHelper.lerp(tickDelta, pointB.prevYaw, pointB.getBodyYaw()) * 0.017453292F) + 1.5707963267948966;
        Vec3d vec3d2 = entity.getLeashOffset(tickDelta);
        double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
        double f = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;
        double g = MathHelper.lerp(tickDelta, entity.prevX, entity.getX()) + e;
        double h = MathHelper.lerp(tickDelta, entity.prevY, entity.getY()) + vec3d2.y;
        double i = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ()) + f;
        matrices.translate(e, vec3d2.y, f);
        float j = (float) (vec3d.x - g);
        float k = (float) (vec3d.y - h);
        float l = (float) (vec3d.z - i);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float n = MathHelper.inverseSqrt(j * j + l * l) * 0.025F * 1.5f;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = BlockPos.ofFloored(entity.getCameraPosVec(tickDelta));
        BlockPos blockPos2 = BlockPos.ofFloored(pointA.getCameraPosVec(tickDelta));
        int q = 0;
        int r = pointA.getWorld().getLightLevel(LightType.BLOCK, pointA.getBlockPos());
        int s = pointA.getWorld().getLightLevel(LightType.SKY, blockPos);
        int t = pointA.getWorld().getLightLevel(LightType.SKY, blockPos2);

        int u;
        for (u = 0; u <= 24; ++u) {
            renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.025F, o, p, u, false, distance);
        }

        for (u = 24; u >= 0; --u) {
            renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025F, 0.0F, o, p, u, true, distance);
        }

        matrices.pop();
    }


    private static void renderLeashPiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float deltaHeight, float h, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight, int holdingEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot, float distance) {
        float m = (float) pieceIndex / 24;

        float q = pieceIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.9F : 1.0F;
        float q2 = pieceIndex % 3 == (isLeashKnot ? 0 : 1) ? 0.8F : 1.0F;
        float r = 0.4F * q * q2;
        float g = 0.4F * q * q2;
        float b = 0.4F * q * q2;

        float u = f * m;
        float v = (deltaHeight * m);
        float w = h * m;
        vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(r, g, b, 1.0F).light(15728640).next();
        vertexConsumer.vertex(positionMatrix, u + k, v + i - j, w - l).color(r, g, b, 1.0F).light(15728640).next();
    }

    @Override
    public Identifier getTexture(MetalCableEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(MetalCableEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
