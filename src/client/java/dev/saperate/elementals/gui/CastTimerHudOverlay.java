package dev.saperate.elementals.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.data.ClientBender;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;

public class CastTimerHudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if(!ClientBender.get().isCasting()){
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        int x = client.getWindow().getScaledWidth()/2;
        int y = client.getWindow().getScaledHeight()/2;

        drawContext.drawCenteredTextWithShadow(client.textRenderer, String.format("%.1f",ClientBender.get().getCastTime()), x + (int) client.getWindow().getScaleFactor(), y + 10, 0xFFFFFFFF);
    }
}
