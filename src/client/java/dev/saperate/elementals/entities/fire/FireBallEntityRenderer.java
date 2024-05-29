package dev.saperate.elementals.entities.fire;

import com.mojang.blaze3d.systems.RenderSystem;
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
    public static long firstTime = -1;

    private static final Identifier fireCoreTex = new Identifier("minecraft", "block/shroomlight");
    private static final Identifier blueFireCoreTex = new Identifier("elementals", "block/bluefire_core");

    public FireBallEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FireBallEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(firstTime == -1){
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 1000;

        matrices.push();


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());
        //Use soul fire for blue fire

        BlockState state = entity.isBlue() ? Blocks.SOUL_FIRE.getDefaultState() : Blocks.FIRE.getDefaultState();


        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(-rot)));
        matrices.translate(-0.5f, 0, -0.5f);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state, entity.getBlockPos(), entity.getWorld(), matrices, vertexConsumer, false, entity.getEntityWorld().random);

        Matrix4f mat = new Matrix4f();



        matrices.translate(0.5f,.5f,0);
        mat.translate(0,0,.5f);
        mat.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(rot * 2)));
        mat.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(rot)));
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
        matrices.pop();
    }


    @Override
    public Identifier getTexture(FireBallEntity entity) {
        return null;
    }
}
