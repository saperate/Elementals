package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(SpectatorGui.class)
public abstract class SpectatorMenuCommandMixin {

    @Shadow
    @Nullable
    private SpectatorMenu menu;

    @Inject(at = @At("HEAD"), method = "renderPage", cancellable = true)
    private void closeMenuIfFakeSpectatorMode(GuiGraphics guiGraphics, float alpha, int x, int y, SpectatorPage spectatorPage, CallbackInfo ci) {
        if(menu != null && safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION, Minecraft.getInstance().player)){
            menu.exit();
            ci.cancel();
        }
    }

}