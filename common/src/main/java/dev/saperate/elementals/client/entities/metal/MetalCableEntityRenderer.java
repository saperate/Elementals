package dev.saperate.elementals.client.entities.metal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.metal.MetalCableEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;


public class MetalCableEntityRenderer extends EntityRenderer<MetalCableEntity> {
    private static final ResourceLocation fireTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/iron_block");//"block/fire_0");

    public MetalCableEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MetalCableEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (entity.getParent() == null) {
            return;
        }

        if (entity.getParent() == null) {
            entity.setPos(entity.getOwner().getRopeHoldPosition(tickDelta));
        }

        matrices.pushPose();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.cutout());
        
        Vec3 pointB = entity.position().subtract(0,0.5,0);
        Vec3 pointA = entity.getParent().getOwner().position().add(0,0.5,0);
        entity.prevDir = renderCubeFromAToB(pointA,pointB,matrices,vertexConsumer,0.125f,entity.prevDir, tickDelta);
        
        matrices.popPose();
    }


    private static Vec3 renderCubeFromAToB(Vec3 pointA, Vec3 pointB,PoseStack matrices, VertexConsumer vertexConsumer, float size, Vec3 prevDir, float tickDelta){
        Matrix4f mat = new Matrix4f();

        Vec3 dir = prevDir.lerp(pointA.subtract(pointB).normalize(),tickDelta);
        matrices.scale(size, size, size);
        matrices.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));
        
        
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
    public ResourceLocation getTextureLocation(MetalCableEntity entity) {
        return null;
    }
    

    @Override
    public boolean shouldRender(MetalCableEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
