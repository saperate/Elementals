package dev.saperate.elementals.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockRenderView;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.function.Function;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;
import static org.lwjgl.opengl.GL11.*;

public class WaterCubeEntityRenderer extends EntityRenderer<WaterCubeEntity> {
    private static final Identifier texture = new Identifier("minecraft", "block/water_still");

    public WaterCubeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterCubeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 0.9f, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        int color = BiomeColors.getWaterColor(entity.getWorld(),entity.getBlockPos());




        drawCube(vertexConsumer, matrices, light,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.9f,
                texture,
                0.9f,
                new Matrix4f().rotate((float) Math.toRadians(90),1,0,0)
        );

        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(WaterCubeEntity entity) {
        return texture;
    }
}
