package dev.saperate.elementals.entities.water;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class WaterJetEntityRenderer extends EntityRenderer<WaterJetEntity> {
    private static final Identifier texture = new Identifier("minecraft", "block/water_still");
    public static long firstTime = -1;


    public WaterJetEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WaterJetEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (firstTime == -1) {
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime) / 1000;

        Entity owner = entity.getOwner();
        WaterJetEntity child = entity.getChild();
        if (child == null || owner == null) {
            return;
        }
        entity.setPosition(getEntityLookVector(owner, 0.5f).subtract(0,0.5f,0));

        float streamSize = entity.getStreamSize();
        matrices.push();
        matrices.scale(0.25f * streamSize, 0.25f * streamSize, 0.25f * streamSize);
        //matrices.translate(0, 0.5f, 0);


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTranslucentMovingBlock());

        int color = BiomeColors.getWaterColor(entity.getWorld(), entity.getBlockPos());


        Vec3d dir = child.getPos().subtract(entity.getPos());
        float d = (float) dir.length() * 4/streamSize;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));

        if (false) {//can use upgrade for more dmg
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) Math.toDegrees(rot * 4)));
        }else{
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) Math.toDegrees(rot)));
        }

        drawCube(vertexConsumer, matrices, light,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                0.9f,
                texture,
                d, mat,
                false,
                true,
                true
        );

        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(WaterJetEntity entity) {
        return texture;
    }
}
