package dev.saperate.elementals.entities.earth;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.models.earth.ShrapnelModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ShrapnelEntityRenderer extends EntityRenderer<EarthShrapnelEntity> {
    private static final Identifier texture = new Identifier("minecraft", "textures/block/dirt.png");
    public ShrapnelEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(EarthShrapnelEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        BlockState state = entity.getBlockState();

        String s = MinecraftClient.getInstance().getBlockRenderManager().getModel(state).toString();
        System.out.println(s);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(getTexture(entity)));

        ShrapnelModel.getTexturedModelData().createModel().render(
                matrices,vertexConsumer,light,0,
                1,
                1,
                1,
                1
        );
        RenderSystem.disableBlend();
        matrices.pop();
    }



    @Override
    public Identifier getTexture(EarthShrapnelEntity entity) {
        return texture;
    }
}
