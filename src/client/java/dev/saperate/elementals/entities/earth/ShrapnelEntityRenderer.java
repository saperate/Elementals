package dev.saperate.elementals.entities.earth;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.models.earth.ShrapnelModel;
import dev.saperate.elementals.entities.models.water.WaterBladeModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ShrapnelEntityRenderer extends EntityRenderer<ShrapnelEntity> {

    public ShrapnelEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(ShrapnelEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(-0.5f, 0, -0.5f);


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        BlockState state = entity.getBlockState();

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
    public Identifier getTexture(ShrapnelEntity entity) {
        return null;
    }
}
