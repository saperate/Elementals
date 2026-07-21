package dev.saperate.elementals.client.entities.fire;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saperate.elementals.entities.fire.FireWhipEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static dev.saperate.elementals.client.entities.utils.RenderUtils.drawCube;

/**
 * Draws the FireWhip "crack" as a fan of fire-textured planes swinging across in front of
 * the player - a cheap but real 3D artifice for a "cut" of flame, using the same crossed-plane
 * trick as a Minecraft fire block rather than a modeled character.
 */
public class FireWhipEntityRenderer extends EntityRenderer<FireWhipEntity> {
    private static final ResourceLocation fireTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/fire_0");
    private static final ResourceLocation soulFireTex = ResourceLocation.fromNamespaceAndPath("minecraft", "block/soul_fire_0");

    public FireWhipEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FireWhipEntity entity, float entityYaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.cutout());

        //orient towards the direction the whip was thrown
        matrices.mulPose(Axis.YP.rotationDegrees(-entity.getSlashYaw()));
        matrices.mulPose(Axis.XP.rotationDegrees(entity.getSlashPitch()));

        float age = entity.tickCount + tickDelta;
        //quick swing across (roughly -60 to +60 degrees) plus a growing/fading alpha so it
        //reads as one fast slash rather than a static sprite
        float swing = -60f + (age / (float) Math.max(1, entity.maxLifeTime)) * 120f;
        float alpha = 1f - (age / (float) Math.max(1, entity.maxLifeTime));

        ResourceLocation tex = entity.isBlue() ? soulFireTex : fireTex;

        for (int i = -1; i <= 1; i++) {
            Matrix4f mat = new Matrix4f();
            mat.rotate(Axis.YP.rotationDegrees(swing + i * 18f));
            mat.translate(0, 0, 1.1f);
            mat.scale(1.6f, 1.6f, 1.6f);

            drawCube(vertexConsumer, matrices, light,
                    1f, entity.isBlue() ? 0.6f : 0.75f, entity.isBlue() ? 1f : 0.25f, alpha * 0.85f,
                    tex, 1f, mat,
                    true, false, false
            );
        }

        RenderSystem.disableBlend();
        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FireWhipEntity entity) {
        return entity.isBlue() ? soulFireTex : fireTex;
    }
}