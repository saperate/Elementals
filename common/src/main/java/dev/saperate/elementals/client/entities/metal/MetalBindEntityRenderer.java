package dev.saperate.elementals.client.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.client.entities.utils.RenderUtils;
import dev.saperate.elementals.entities.metal.MetalBindEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;


public class MetalBindEntityRenderer extends EntityRenderer<MetalBindEntity> {
    private static final ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/iron_block");//"block/fire_0");

    public MetalBindEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MetalBindEntity entity, float entityYaw, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.getChild() == null) {
            return;
        }
        
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());
        
        Vec3 pointA = entity.getOwner().getEyePosition();//Other
        Vec3 pointB = entity.getChild().getOwner().getRopeHoldPosition(tickDelta);//Player
        
        double distance = pointA.distanceTo(pointB);
        
        int segmentCount = (int) (distance*2) + 5;
        for (int i = 0; i < segmentCount; i++) {
            Vec3 currentPos = getNodePos((float) i /segmentCount,pointA,pointB,distance);
            Vec3 nextPos = getNodePos((float) (i + 1) /segmentCount,pointA,pointB,distance);

            
            renderCubeFromAToB(pointA,currentPos,nextPos,poseStack,vertexConsumer,0.125f);
            
        }
    }


    private static void renderCubeFromAToB(Vec3 origin, Vec3 pointA, Vec3 pointB,PoseStack matrices, VertexConsumer vertexConsumer, float size){
        Matrix4f mat = new Matrix4f();
        matrices.pushPose();
        Vec3 dir = pointB.subtract(pointA).normalize();
        matrices.translate(pointA.x - origin.x, pointA.y - origin.y,pointA.z - origin.z);
        matrices.scale(size, size, size);
        matrices.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));
        matrices.translate(0, -1/(size), 0);

        RenderUtils.drawCube(vertexConsumer, matrices, 255,
                0.4f,
                0.4f,
                0.4f,
                1F,
                tex,
                Math.max((float) (pointA.distanceTo(pointB) * 1.5/size),2), mat,
                false,
                true,
                true
        );
        matrices.popPose();
    }
    
    private static Vec3 getNodePos(float delta, Vec3 pointA, Vec3 pointB, double distance){
        return pointA.lerp(pointB, delta).add(0, Mth.lerp(distance/18,Math.pow(delta,2),0.5) + 1,0);
    }

    @Override
    public ResourceLocation getTextureLocation(MetalBindEntity entity) {
        return tex;
    }

    @Override
    public boolean shouldRender(MetalBindEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
