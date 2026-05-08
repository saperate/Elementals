package dev.saperate.elementals.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.models.metal.MetalLanceModel;
import dev.saperate.elementals.entities.models.water.WaterBladeModel;
import dev.saperate.elementals.entities.water.WaterBladeEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.ElementalsClient.MODEL_METAL_LANCE_LAYER;
import static dev.saperate.elementals.ElementalsClient.MODEL_WATER_BLADE_LAYER;

public class MetalLanceRenderer extends EntityRenderer<MetalLanceEntity> implements FeatureRendererContext<MetalLanceEntity, MetalLanceModel> {
    private static final Identifier texture = Identifier.of(MODID, "textures/entity/metal_lance.png");
    private final MetalLanceModel model;
    public static long firstTime = -1;

    public MetalLanceRenderer(EntityRendererFactory.Context context) {
        super(context);
        model = new MetalLanceModel(context.getPart(MODEL_METAL_LANCE_LAYER));
    }

    @Override
    public void render(MetalLanceEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (firstTime == -1) {
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime);
        matrices.push();


        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(getTexture(entity)));

        if (entity.getOwner() != null && entity.getIsControlled()) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getOwner().getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getOwner().getPitch()));
        }else{
            double d = entity.getDeltaMovement().horizontalLength();
            float entityYaw = ((float)(MathHelper.atan2(entity.getDeltaMovement().x, entity.getDeltaMovement().z) * 57.2957763671875));
            float pitch = ((float)(MathHelper.atan2(entity.getDeltaMovement().y, d) * 57.2957763671875));
            
            
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entityYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-pitch));
        }
        matrices.translate(0, -1.325, 0);

        MetalLanceModel.getTexturedModelData().createModel().render(
                matrices, vertexConsumer, light, 0, 0xFF555555
        );


        RenderSystem.disableBlend();
        matrices.pop();

    }

    @Override
    public Identifier getTexture(MetalLanceEntity entity) {
        return texture;
    }

    @Override
    public MetalLanceModel getModel() {
        return this.model;
    }


}
