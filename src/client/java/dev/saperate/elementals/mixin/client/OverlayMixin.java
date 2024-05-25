package dev.saperate.elementals.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.water.AbilityWaterShield;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.saperate.elementals.effects.DrowningStatusEffect.DROWNING_EFFECT;

@Mixin(InGameOverlayRenderer.class)
public class OverlayMixin {
    @Shadow
    private static Identifier UNDERWATER_TEXTURE;

    @Inject(at = @At("HEAD"), method = "renderOverlays")
    private static void renderWaterOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        ClientBender bender = ClientBender.get();
        if (bender != null && bender.player != null) {
            if (bender.currAbility instanceof AbilityWaterShield) {
                renderUnderwaterOverlay(client, matrices);
            } else if (bender.player.hasStatusEffect(DROWNING_EFFECT)) {
                int colorId = bender.player.getStatusEffect(DROWNING_EFFECT).getAmplifier();
                if(colorId == 100){
                    return;
                }
                renderUnderwaterOverlay(client,matrices);
            }
        }
    }

    @Unique
    private static void renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices) {
        renderUnderwaterOverlay(client, matrices,4);
    }

    @Unique
    private static void renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, int blueMultiplier) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, UNDERWATER_TEXTURE);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        BlockPos blockPos = BlockPos.ofFloored((double) client.player.getX(), (double) client.player.getEyeY(), (double) client.player.getZ());
        float f = LightmapTextureManager.getBrightness(client.player.getWorld().getDimension(), client.player.getWorld().getLightLevel(blockPos));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(f, f, f * blueMultiplier, 0.5f);

        float m = -client.player.getYaw() / 64.0f;
        float n = client.player.getPitch() / 64.0f;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).texture(4.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).texture(0.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).texture(0.0f + m, 0.0f + n).next();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).texture(4.0f + m, 0.0f + n).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}