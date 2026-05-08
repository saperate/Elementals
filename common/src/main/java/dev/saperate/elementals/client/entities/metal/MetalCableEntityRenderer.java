package dev.saperate.elementals.client.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import dev.saperate.elementals.entities.metal.MetalCableEntity;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3;
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
        if (entity.getParent() == null) {
            return;
        }

        if (entity.getParent() == null) {
            entity.setPosition(entity.getOwner().getLeashPos(tickDelta));
        }

        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());
        
        Vec3 pointB = entity.getPos().subtract(0,0.5,0);
        Vec3 pointA = entity.getParent().getOwner().getPos().add(0,0.5,0);
        entity.prevDir = renderCubeFromAToB(pointA,pointB,matrices,vertexConsumer,0.125f,entity.prevDir, tickDelta);
        
        matrices.pop();
    }


    private static Vec3 renderCubeFromAToB(Vec3 pointA, Vec3 pointB,MatrixStack matrices, VertexConsumer vertexConsumer, float size, Vec3 prevDir, float tickDelta){
        Matrix4f mat = new Matrix4f();

        Vec3 dir = prevDir.lerp(pointA.subtract(pointB).normalize(),tickDelta);
        matrices.scale(size, size, size);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));
        
        
        drawCube(vertexConsumer, matrices, 255,
                0.4f,
                0.4f,
                0.4f,
                1F,
                fireTex,
                (float) (pointA.distanceTo(pointB)/size), mat,
                false,
                true,
                true
        );
        
        return dir;
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
