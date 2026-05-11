package dev.saperate.elementals.client.entities.water;

import com.mojang.math.Axis;
import dev.saperate.elementals.entities.water.WaterHelmetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

public class WaterHelmetEntityRenderer extends EntityRenderer<WaterHelmetEntity> {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

    private static final ResourceLocation airTexture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block");
    private static final ResourceLocation airTopTexture = ResourceLocation.fromNamespaceAndPath("elementals", "block/air_block_top");

    public WaterHelmetEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WaterHelmetEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        LivingEntity owner = entity.getOwner();
        Vec3 eyePos = owner.getEyePosition(tickDelta);
        entity.setPos(eyePos.x,eyePos.y - 0.5f, eyePos.z);

        if(!entity.isOwnerBiped()){
            return;
        }

        matrices.pushPose();
        matrices.translate(0, 0.8f, 0);



        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.translucentMovingBlock());

        Matrix4f mat = new Matrix4f();
        mat.translate(0,-0.30f,0.325f);
        mat.rotate(Axis.YP.rotationDegrees(-owner.yHeadRot));
        mat.rotate(Axis.XP.rotationDegrees(owner.getXRot()));
        mat.translate(0,0.325f,-0.325f);
        matrices.translate(0,-0.30f,-0.325f);
        mat.scale(0.65f);

        if(entity.getModelId() == 0){
            int color = BiomeColors.getAverageWaterColor(entity.level(),entity.getOnPos());
            drawCube(vertexConsumer, matrices, light,
                    (color >> 16 & 255) / 255.0f,
                    (color >> 8 & 255) / 255.0f,
                    (color & 255) / 255.0f,
                    0.8f,
                    texture,
                    1,
                    mat,
                    false,
                    true,
                    true
            );
        }else{
            //TODO make a 90* rotation so it looks better
            drawCube(vertexConsumer, matrices, light,
                    1,
                    1,
                    1,
                    0.8f,
                    airTexture,
                    airTopTexture,
                    1,
                    mat,
                    false,
                    true,
                    true
            );
        }




        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(WaterHelmetEntity entity) {
        return texture;
    }


}
