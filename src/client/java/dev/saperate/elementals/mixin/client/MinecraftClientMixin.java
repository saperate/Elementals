package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {


    @Inject(at = @At("HEAD"), method = "hasOutline", cancellable = true)
    private void closeMenuIfFakeSpectatorMode(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (safeHasStatusEffect(SPIRIT_PROJECTION_EFFECT, MinecraftClient.getInstance().player)) {
            cir.setReturnValue(entity.isGlowing()
                    || (entity instanceof DecoyPlayerEntity decoy
                    && decoy.getOwnerUUID().equals(MinecraftClient.getInstance().player.getUuid()))
                    && MinecraftClient.getInstance().options.spectatorOutlinesKey.isPressed()
            );
        }
    }

}