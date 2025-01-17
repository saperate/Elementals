package dev.saperate.elementals.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static dev.saperate.elementals.entities.utils.RenderUtils.drawCube;


public class MetalCableEntityRenderer extends EntityRenderer<MetalCableEntity> {
    private static final Identifier fireTex = Identifier.of("minecraft", "block/iron_block");//"block/fire_0");
    public MetalCableEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(MetalCableEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        MetalCableEntity child = entity.getChild();
        if (child == null) {
            entity.setPosition(entity.getOwner().getPos().add(0,1,0));
            return;
        }

        if(entity.getParent() == null){
            entity.setPosition(entity.getOwner().getPos().add(0,1,0));
        }

        matrices.push();
        matrices.scale(0.125f, 0.125f, 0.125f);
        //matrices.translate(0, 0.5f, 0);


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        int color = 0xFF303030;


        Vec3d dir = child.getPos().subtract(entity.getPos());
        float d = (float) dir.length() * 8;
        dir = dir.normalize();


        Matrix4f mat = new Matrix4f();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(Math.atan2(dir.x, dir.z))));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) Math.toDegrees(Math.asin(-dir.y))));


        drawCube(vertexConsumer, matrices, 255,
                (color >> 16 & 255) / 255.0f,
                (color >> 8 & 255) / 255.0f,
                (color & 255) / 255.0f,
                1,
                Identifier.of("minecraft", "block/iron_block"),
                d * 1.05f, mat,
                false,
                true,
                true
        );


        RenderSystem.disableBlend();
        matrices.pop();
    }

    @Override
    public Identifier getTexture(MetalCableEntity entity) {
        return null;
    }
}
