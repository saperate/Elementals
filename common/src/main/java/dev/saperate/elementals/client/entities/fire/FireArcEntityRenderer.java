package dev.saperate.elementals.client.entities.fire;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;


public class FireArcEntityRenderer extends EntityRenderer<FireArcEntity> {
    private static final ResourceLocation fireTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/fire_0");//"block/fire_0");
    private static final ResourceLocation blueFireTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_fire_0");//"block/fire_0");
    private static final ResourceLocation fireCoreTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/shroomlight");
    private static final ResourceLocation blueFireCoreTex = ResourceLocation.fromNamespaceAndPath("elementals", "block/bluefire_core");

    public FireArcEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FireArcEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        FireArcEntity child = entity.getChild();
        if (child == null) {
            return;
        }

        matrices.pushPose();
        matrices.scale(0.25f, 0.25f, 0.25f);
        //matrices.translate(0, 0.5f, 0);


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.cutout());

        int color = 0xFFFFFF;


        Vec3 dir = child.position().subtract(entity.position());
        float d = (float) dir.length() * 4;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));


        drawCube(vertexConsumer, matrices, 255,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                1,
                entity.isBlue() ? blueFireTex : fireTex,
                d, mat,
                false,
                false,
                false
        );

        matrices.scale(0.8f, 0.8f, 0.8f);

        int color2 = entity.isBlue() ? 0xffffff : 0xfff600;
        drawCube(vertexConsumer, matrices, 255,
                (color2 >> 16 & 255) / 255.0f,
                (color2 >> 8 & 255) / 255.0f,
                (color2 & 255) / 255.0f,
                0.5f,
                 entity.isBlue() ? blueFireCoreTex : fireCoreTex,
                d * 1.25f, mat,
                false,
                true,
                true
        );


        RenderSystem.disableBlend();
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FireArcEntity entity) {
        return fireTex;
    }
}
