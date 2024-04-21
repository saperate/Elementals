package dev.saperate.elementals.entities;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.utils.renderlayers.WaterHelmetRenderLayer;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import dev.saperate.elementals.entities.water.WaterHelmetEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;
import static dev.saperate.elementals.entities.utils.RenderUtils.drawInvertedCube;

public class WaterHelmetEntityRenderer extends EntityRenderer<WaterHelmetEntity> {
    private static final Identifier texture = new Identifier("minecraft", "block/water_flow");

    public WaterHelmetEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterHelmetEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Entity owner = entity.getOwner();
        Vec3d eyePos = owner.getEyePos();
        entity.setPos(eyePos.x,eyePos.y - 0.5f, eyePos.z);


        matrices.push();
        matrices.translate(0, 0.8f, 0);



        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        int color = BiomeColors.getWaterColor(entity.getWorld(),entity.getBlockPos());

        Matrix4f mat = new Matrix4f();
        mat.translate(0,-0.30f,0.325f);
        mat.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(-owner.getYaw()));
        mat.rotate(RotationAxis.POSITIVE_X.rotationDegrees(owner.getPitch()));
        mat.translate(0,0.325f,-0.325f);
        matrices.translate(0,-0.30f,-0.325f);
        mat.scale(0.65f);

        drawCube(vertexConsumer, matrices, light,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.9f,
                texture,
                1,
                mat,
                false,
                true,
                true
        );



        matrices.pop();
    }

    @Override
    public Identifier getTexture(WaterHelmetEntity entity) {
        return texture;
    }


}
