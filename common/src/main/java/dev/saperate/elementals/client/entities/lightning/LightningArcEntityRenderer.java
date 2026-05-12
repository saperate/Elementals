package dev.saperate.elementals.client.entities.lightning;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.entities.lightning.LightningArcEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;


public class LightningArcEntityRenderer extends EntityRenderer<LightningArcEntity> {
    private static final ResourceLocation tex = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "block/lightning_block");//"block/fire_0");

    public LightningArcEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LightningArcEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        LightningArcEntity child = entity.getChild();
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

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());


        Vec3 dir = child.position().subtract(entity.position());
        float d = (float) dir.length() * 4;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));

        for (int i = 1; i < 5; i++) {
            Matrix4f nMat = new Matrix4f(mat);
            nMat.scale((float)i / 4,(float)i / 4,(float)i / 4);
            drawCube(vertexConsumer, matrices, 255,
                    1,
                    1,
                    1,
                    (float)(4 / i) / 4,
                    tex,
                    d * (1 / ((float)i / 4)), nMat,
                    false,
                    true,
                    true
            );

        }



        RenderSystem.disableBlend();
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(LightningArcEntity entity) {
        return tex;
    }
}
