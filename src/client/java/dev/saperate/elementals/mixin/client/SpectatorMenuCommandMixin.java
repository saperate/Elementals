package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(SpectatorHud.class)
public abstract class SpectatorMenuCommandMixin {
    @Shadow private @Nullable SpectatorMenu spectatorMenu;

    @Inject(at = @At("HEAD"), method = "renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V", cancellable = true)
    private void closeMenuIfFakeSpectatorMode(DrawContext context, CallbackInfo ci) {
        if(spectatorMenu != null && safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION, MinecraftClient.getInstance().player)){
            spectatorMenu.close();
            ci.cancel();
        }
    }

}