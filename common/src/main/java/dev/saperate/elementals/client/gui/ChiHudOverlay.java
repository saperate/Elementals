package dev.saperate.elementals.client.gui;

import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.utils.MathHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

import static dev.saperate.elementals.Constants.MODID;

public class ChiHudOverlay implements LayeredDraw.Layer {

    
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (ClientBender.get().chi >= ElementalConfig.get().CHI_OVERLAY_THRESHOLD) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        int x = (int) (client.getWindow().getGuiScaledWidth() - client.getWindow().getGuiScale() * 16);
        int y = (int) (client.getWindow().getGuiScaledHeight() - 8 * client.getWindow().getGuiScale());

        ResourceLocation symbolID = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/symbol/" + ClientBender.get().getElement().getName().toLowerCase(Locale.ROOT) + ".png");
        ResourceLocation buttonID = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/" + ClientBender.get().getElement().getName().toLowerCase(Locale.CANADA) + "_upgrade_button.png");
        
        graphics.blit(buttonID, x - 40, y - 18, 0, 0, 32, 32, 32, 32);
        graphics.blit(symbolID, x - 40, y - 18, 0, 0, 32, 32, 32, 32);

        float chi = MathHelper.clamp(ClientBender.get().chi/ElementalConfig.get().MAX_CHI * 100, 0, 100);

        int height = (int) Math.floor(chi) / 2;
        int maxHeight = 50;

        graphics.blit(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chi.png"), x + 3, y - height + 7 + 3, 0, 0, 16, height, 16, 32);
        
        if(height > 5)
            graphics.blit(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chi_foam.png"), x + 4, y - height + 10, 0, 0, 16, 16, 16, 16);

        graphics.blit(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chi_frame.png"), x, y - maxHeight + 7, 0, 0, 22, maxHeight + 6, 32, 56);

        graphics.blit(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chi_frame.png"), x, y - maxHeight + 5, 0, 0, 22, maxHeight + 6, 32, 56);

        
        //TODO add config that toggles between number and bar
        if(ElementalConfig.get().CHI_OVERLAY_TEXT){
            graphics.drawCenteredString(client.font, String.format("%.2f", chi), x - 24, y - 25, 0xFFFFFFFF);
        }
    }

}
