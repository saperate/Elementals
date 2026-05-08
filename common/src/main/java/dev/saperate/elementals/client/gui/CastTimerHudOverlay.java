package dev.saperate.elementals.client.gui;

import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.data.ElementalConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class CastTimerHudOverlay implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if(!ClientBender.get().isCasting() || ElementalConfig.get().HIDE_TIMER){
            return;
        }
        Minecraft client = Minecraft.getInstance();
        int x = client.getWindow().getGuiScaledWidth()/2;
        int y = client.getWindow().getGuiScaledHeight()/2;

        graphics.drawCenteredString(client.font, String.format("%.1f",ClientBender.get().getCastTime()), x + (int) client.getWindow().getGuiScale(), y + 10, 0xFFFFFFFF);
    }
}
