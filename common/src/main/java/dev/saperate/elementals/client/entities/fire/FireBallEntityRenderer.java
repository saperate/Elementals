package dev.saperate.elementals.client.entities.fire;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;


import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

public class FireBallEntityRenderer extends EntityRenderer<FireBallEntity> {
    public static long firstTime = -1;

    private static final ResourceLocation fireCoreTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/shroomlight");
    private static final ResourceLocation blueFireCoreTex = ResourceLocation.fromNamespaceAndPath("elementals", "block/bluefire_core");

    public FireBallEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FireBallEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 1000;

        matrices.pushPose();


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.cutout());
        //Use soul fire for blue fire

        BlockState state = entity.isBlue() ? Blocks.SOUL_FIRE.defaultBlockState() : Blocks.FIRE.defaultBlockState();


        matrices.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(-rot)));
        matrices.translate(-0.5f, 0, -0.5f);
        Minecraft.getInstance().getBlockRenderer().renderBatched(state, entity.getOnPos(), entity.level(), matrices, vertexConsumer, false, entity.level().random);

        Matrix4f mat = new Matrix4f();



        matrices.translate(0.5f,.5f,0);
        mat.translate(0,0,.5f);
        mat.rotate(Axis.YP.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat.rotate(Axis.XP.rotationDegrees((float) Math.toDegrees(rot)));
        mat.translate(0,0,-.5f);

        matrices.scale(.9f,.9f,.9f);

        int color2 = entity.isBlue() ? 0xffffff : 0xfff600;
        drawCube(vertexConsumer, matrices, 255,
                (color2 >> 16 & 255) / 255.0f,
                (color2 >> 8 & 255) / 255.0f,
                (color2 & 255) / 255.0f,
                0.5f,
                entity.isBlue() ? blueFireCoreTex : fireCoreTex,
                1, mat,
                false,
                true,
                true
        );

        RenderSystem.disableBlend();
        matrices.popPose();
    }


    @Override
    public ResourceLocation getTextureLocation(FireBallEntity entity) {
        return fireCoreTex;
    }
}
