package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


@Mixin(LivingEntity.class)
public abstract class GlowMixin {


    @Shadow
    public abstract void remove(Entity.RemovalReason reason);

    @Inject(at = @At("HEAD"), method = "isCurrentlyGlowing", cancellable = true)
    private void render(CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        LivingEntity e = ((LivingEntity) (Object) this);
        if (safeHasStatusEffect(ElementalsStatusEffects.SEISMIC_SENSE, player) && e.level().isClientSide && e.onGround()
                && player.onGround()
                && !player.equals(e)
                && e.position().subtract(player.position()).length() <= 60) { //TODO add upgrades for range
            cir.setReturnValue(true);
        }
    }
}