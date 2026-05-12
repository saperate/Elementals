package dev.saperate.elementals.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.water.AbilityWaterShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class OverlayMixin {

    @Shadow
    @Final
    private static ResourceLocation UNDERWATER_LOCATION;

    @Inject(at = @At("HEAD"), method = "renderScreenEffect")
    private static void renderWaterOverlay(Minecraft client, PoseStack matrices, CallbackInfo ci) {
        ClientBender bender = ClientBender.get();
        if (bender != null && bender.player != null) {
            if (bender.currAbility instanceof AbilityWaterShield) {
                elementals$renderUnderwaterOverlay(client, matrices);
            } else if (bender.player.hasEffect(ElementalsStatusEffects.DROWNING.get())) {
                int colorId = bender.player.getEffect(ElementalsStatusEffects.DROWNING.get()).getAmplifier();
                if(colorId == 100){
                    return;
                }
                elementals$renderUnderwaterOverlay(client,matrices);
            }
        }
    }

    @Unique
    private static void elementals$renderUnderwaterOverlay(Minecraft client, PoseStack matrices) {
        elementals$renderUnderwaterOverlay(client, matrices,4);
    }

    @Unique
    private static void elementals$renderUnderwaterOverlay(Minecraft client, PoseStack matrices, int blueMultiplier) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, UNDERWATER_LOCATION);
        if(client.player == null){
            return;
        }
        BlockPos blockPos = BlockPos.containing((double) client.player.getX(), (double) client.player.getEyeY(), (double) client.player.getZ());
        float f = LightTexture.getBrightness(client.player.level().dimensionType(), client.player.level().getLightEmission(blockPos));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(f, f, f * blueMultiplier, 0.5f);

        float m = -client.player.getYRot() / 64.0f;
        float n = client.player.getXRot() / 64.0f;
        Matrix4f matrix4f = matrices.last().pose();

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -1.0f, -1.0f, -0.5f).setUv(4.0f + m, 4.0f + n);
        bufferBuilder.addVertex(matrix4f, 1.0f, -1.0f, -0.5f).setUv(0.0f + m, 4.0f + n);
        bufferBuilder.addVertex(matrix4f, 1.0f, 1.0f, -0.5f).setUv(0.0f + m, 0.0f + n);
        bufferBuilder.addVertex(matrix4f, -1.0f, 1.0f, -0.5f).setUv(4.0f + m, 0.0f + n);
        BufferUploader.drawWithShader(bufferBuilder.build());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}