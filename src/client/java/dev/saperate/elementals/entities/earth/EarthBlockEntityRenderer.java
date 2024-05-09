package dev.saperate.elementals.entities.earth;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import dev.saperate.elementals.entities.models.earth.ShrapnelModel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class EarthBlockEntityRenderer extends EntityRenderer<EarthBlockEntity> {
    private static final Identifier texture = new Identifier("minecraft", "textures/block/dirt.png");
    public EarthBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(EarthBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(-0.5f, 0, -0.5f);


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if(!entity.IsShrapnel()){
            BlockState state = entity.getBlockState();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(state));
            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state, entity.getBlockPos(), entity.getWorld(), matrices, vertexConsumer, false, entity.getEntityWorld().random);

        }else{
            matrices.translate(0.5f, -1, 0.5f);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(getTexture(entity)));

            Vec3d dir = entity.getVelocity();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x,dir.z))));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));

            ShrapnelModel.getTexturedModelData().createModel().render(
                    matrices,vertexConsumer,light,0,
                    1,
                    1,
                    1,
                    1
            );
        }



        RenderSystem.disableBlend();
        matrices.pop();
    }



    @Override
    public Identifier getTexture(EarthBlockEntity entity) {
        return texture;
    }
}
