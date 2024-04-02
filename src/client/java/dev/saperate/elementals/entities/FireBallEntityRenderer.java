package dev.saperate.elementals.entities;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;


import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class FireBallEntityRenderer extends EntityRenderer<FireBallEntity> {

    private static final Identifier fireCoreTex = new Identifier("minecraft", "block/shroomlight");
    private static final Identifier blueFireCoreTex = new Identifier("elementals", "block/bluefire_core");

    public FireBallEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FireBallEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(-0.5f, 0, -0.5f);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());
        //Use soul fire for blue fire

        BlockState state = entity.isBlue() ? Blocks.SOUL_FIRE.getDefaultState() : Blocks.FIRE.getDefaultState();

        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state, entity.getBlockPos(), entity.getWorld(), matrices, vertexConsumer, false, entity.getEntityWorld().random);

        Matrix4f mat = new Matrix4f();


        double rot = Math.sin((double) System.currentTimeMillis() / 100000) * 360;
        matrices.translate(0.5f,0.5f,0.5f); //Matrix stack
        mat.translate(-0.5f,0,.5f);
        mat.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot)));
        mat.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rot)));
        mat.translate(0.5f,0,-.5f);



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
        matrices.pop();
    }


    @Override
    public Identifier getTexture(FireBallEntity entity) {
        return null;
    }
}
