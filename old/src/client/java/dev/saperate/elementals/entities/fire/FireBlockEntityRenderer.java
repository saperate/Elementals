package dev.saperate.elementals.entities.fire;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;

public class FireBlockEntityRenderer extends EntityRenderer<FireBlockEntity> {

    public FireBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FireBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {


        matrices.push();
        matrices.translate(-0.5f, 0, -0.5f);


        matrices.scale(1, entity.prevFlameSize, 1);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());
        //Use soul fire for blue fire

        BlockState state = entity.isBlue() ? Blocks.SOUL_FIRE.getDefaultState() : Blocks.FIRE.getDefaultState();

        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state, entity.getBlockPos(), entity.getWorld(), matrices, vertexConsumer, false, entity.getEntityWorld().random);

        RenderSystem.disableBlend();
        matrices.pop();
    }



    @Override
    public Identifier getTexture(FireBlockEntity entity) {
        return null;
    }
}
