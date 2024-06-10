package dev.saperate.elementals.gui;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.utils.MathHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ChiHudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (ClientBender.get().chi >= 115) {
            return;
        }
        float chi = MathHelper.clamp(ClientBender.get().chi, 0, 100);

        MinecraftClient client = MinecraftClient.getInstance();
        int x = (int) (client.getWindow().getScaledWidth() - client.getWindow().getScaleFactor() * 16);
        int y = (int) (client.getWindow().getScaledHeight() - 8 * client.getWindow().getScaleFactor());

        int height = (int) Math.floor(chi) / 2;
        int maxHeight = 50;

        drawContext.drawTexture(new Identifier(MODID, "textures/gui/chi.png"), x , y - height, 0, 0, 16, height, 32, 32);

        drawContext.drawTexture(new Identifier(MODID, "textures/gui/chi_frame.png"), x - 3, y - maxHeight - 3, 0, 0, 22, maxHeight + 6, 32, 56);


        //TODO add config that toggles between number and bar
        //drawContext.drawCenteredTextWithShadow(client.textRenderer, String.format("%.2f", chi), x, y - 10, 0xFFFFFFFF);
    }
}
