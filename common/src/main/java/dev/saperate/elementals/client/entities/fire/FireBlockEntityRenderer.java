package dev.saperate.elementals.client.entities.fire;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FireBlockEntityRenderer extends EntityRenderer<FireBlockEntity> {

    public FireBlockEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FireBlockEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {


        matrices.pushPose();
        matrices.translate(-0.5f, 0, -0.5f);


        matrices.scale(1, entity.prevFlameSize, 1);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.cutout());
        //Use soul fire for blue fire

        BlockState state = entity.isBlue() ? Blocks.SOUL_FIRE.defaultBlockState() : Blocks.FIRE.defaultBlockState();

        Minecraft.getInstance().getBlockRenderer().renderBatched(state, entity.getOnPos(), entity.level(), matrices, vertexConsumer, false, entity.level().random);

        RenderSystem.disableBlend();
        matrices.popPose();
    }



    @Override
    public ResourceLocation getTextureLocation(FireBlockEntity entity) {
        return null;
    }
}
