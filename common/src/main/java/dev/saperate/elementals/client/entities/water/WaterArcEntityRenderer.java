package dev.saperate.elementals.client.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;


public class WaterArcEntityRenderer extends EntityRenderer<WaterArcEntity> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");

    public WaterArcEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WaterArcEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        WaterArcEntity child = entity.getChild();
        if(child == null){
            return;
        }

        matrices.pushPose();
        matrices.scale(0.25f, 0.25f, 0.25f);
        //matrices.translate(0, 0.5f, 0);


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());

        int color = BiomeColors.getAverageWaterColor(entity.level(),entity.getOnPos());




        Vec3 dir = child.position().subtract(entity.position());
        float d = (float) dir.length() * 4;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x,dir.z))));
        matrices.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));


        drawCube(vertexConsumer,matrices,light,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.9f,
                texture,
                d,mat,
                false,
                true,
                true
        );

        RenderSystem.disableBlend();
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(WaterArcEntity entity) {
        return texture;
    }
}
