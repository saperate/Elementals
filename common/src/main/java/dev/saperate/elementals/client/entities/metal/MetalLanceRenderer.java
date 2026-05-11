package dev.saperate.elementals.client.entities.metal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.client.entities.models.metal.MetalLanceModel;
import dev.saperate.elementals.entities.metal.MetalLanceEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.client.ElementalsClient.MODEL_METAL_LANCE_LAYER;

public class MetalLanceRenderer extends EntityRenderer<MetalLanceEntity> implements RenderLayerParent<MetalLanceEntity, MetalLanceModel> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/metal_lance.png");
    private final MetalLanceModel model;
    public static long firstTime = -1;

    public MetalLanceRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new MetalLanceModel(context.bakeLayer(MODEL_METAL_LANCE_LAYER));
    }

    @Override
    public void render(MetalLanceEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (firstTime == -1) {
            firstTime = System.currentTimeMillis();
        }
        float rot = (float) (System.currentTimeMillis() - firstTime);
        matrices.pushPose();


        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.itemEntityTranslucentCull(getTextureLocation(entity)));

        if (entity.getOwner() != null && entity.getIsControlled()) {
            matrices.mulPose(Axis.YP.rotationDegrees(-entity.getOwner().getYRot()));
            matrices.mulPose(Axis.XP.rotationDegrees(entity.getOwner().getXRot()));
        }else{
            double d = entity.getDeltaMovement().horizontalDistance();
            float entityYaw = ((float)(Mth.atan2(entity.getDeltaMovement().x, entity.getDeltaMovement().z) * 57.2957763671875));
            float pitch = ((float)(Mth.atan2(entity.getDeltaMovement().y, d) * 57.2957763671875));
            
            
            matrices.mulPose(Axis.YP.rotationDegrees(entityYaw));
            matrices.mulPose(Axis.XP.rotationDegrees(-pitch));
        }
        matrices.translate(0, -1.325, 0);

        MetalLanceModel.getTexturedModelData().bakeRoot().render(
                matrices, vertexConsumer, light, 0, 0xFF555555
        );


        RenderSystem.disableBlend();
        matrices.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(MetalLanceEntity entity) {
        return texture;
    }

    @Override
    public MetalLanceModel getModel() {
        return this.model;
    }


}
