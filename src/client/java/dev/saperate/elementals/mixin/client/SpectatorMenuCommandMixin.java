package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.data.Bender;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(SpectatorHud.class)
public abstract class SpectatorMenuCommandMixin {


    @Shadow private @Nullable SpectatorMenu spectatorMenu;

    @Inject(at = @At("HEAD"), method = "renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V", cancellable = true)
    private void closeMenuIfFakeSpectatorMode(DrawContext context, CallbackInfo ci) {
        if(spectatorMenu != null && safeHasStatusEffect(SPIRIT_PROJECTION_EFFECT, MinecraftClient.getInstance().player)){
            spectatorMenu.close();
            ci.cancel();
        }
    }

}