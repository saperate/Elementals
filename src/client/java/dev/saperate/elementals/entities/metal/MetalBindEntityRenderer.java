package dev.saperate.elementals.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;


public class MetalBindEntityRenderer extends EntityRenderer<MetalBindEntity> {
    private static final Identifier fireTex = Identifier.of("minecraft", "block/iron_block");//"block/fire_0");

    public MetalBindEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(MetalBindEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.getChild() == null) {
            return;
        }
        
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());
        
        Vec3d pointA = entity.getOwner().getEyePos();//Other
        Vec3d pointB = entity.getChild().getOwner().getLeashPos(tickDelta);//Player
        
        double distance = pointA.distanceTo(pointB);
        
        int segmentCount = (int) (distance*2) + 5;
        for (int i = 0; i < segmentCount; i++) {
            Vec3d currentPos = getNodePos((float) i /segmentCount,pointA,pointB,distance);
            Vec3d nextPos = getNodePos((float) (i + 1) /segmentCount,pointA,pointB,distance);

            
            renderCubeFromAToB(pointA,currentPos,nextPos,matrices,vertexConsumer,0.125f);
            
        }
    }


    private static void renderCubeFromAToB(Vec3d origin, Vec3d pointA, Vec3d pointB,MatrixStack matrices, VertexConsumer vertexConsumer, float size){
        Matrix4f mat = new Matrix4f();
        matrices.push();
        Vec3d dir = pointB.subtract(pointA).normalize();
        matrices.translate(pointA.x - origin.x, pointA.y - origin.y,pointA.z - origin.z);
        matrices.scale(size, size, size);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));
        matrices.translate(0, -1/(size), 0);

        drawCube(vertexConsumer, matrices, 255,
                0.4f,
                0.4f,
                0.4f,
                1F,
                fireTex,
                Math.max((float) (pointA.distanceTo(pointB) * 1.5/size),2), mat,
                false,
                true,
                true
        );
        matrices.pop();
    }
    
    private static Vec3d getNodePos(float delta, Vec3d pointA, Vec3d pointB, double distance){
        return pointA.lerp(pointB, delta).add(0,MathHelper.lerp(distance/18,Math.pow(delta,2),0.5) + 1,0);
    }

    @Override
    public Identifier getTexture(MetalBindEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(MetalBindEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
