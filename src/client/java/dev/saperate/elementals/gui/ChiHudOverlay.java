package dev.saperate.elementals.gui;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.utils.MathHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ChiHudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        float chi = MathHelper.clamp(ClientBender.get().chi, 0 , 100);
        if(chi >= 200){
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int x = client.getWindow().getScaledWidth();
        int y = client.getWindow().getScaledHeight();

        drawContext.drawCenteredTextWithShadow(client.textRenderer, String.format("%.2f", chi), x - (int) client.getWindow().getScaleFactor() * 20, y - 10, 0xFFFFFFFF);
    }
}
