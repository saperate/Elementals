package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "shouldEntityAppearGlowing", cancellable = true)
    private void closeMenuIfFakeSpectatorMode(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION.get(), Minecraft.getInstance().player)) {
            cir.setReturnValue(entity.isCurrentlyGlowing()
                    || (entity instanceof DecoyPlayerEntity decoy
                    && decoy.getOwnerUUID().equals(Minecraft.getInstance().player.getUUID()))
                    && Minecraft.getInstance().options.keySpectatorOutlines.isDown()
            );
        }
    }

}