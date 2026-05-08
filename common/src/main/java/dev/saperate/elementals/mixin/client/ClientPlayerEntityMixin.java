package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.elements.air.AbilityAirScooter;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin {


    @Inject(at = @At("TAIL"), method = "tick")
    private void render(CallbackInfo ci) {
        Player plr = ((Player) (Object) this);
        if (plr == Minecraft.getInstance().player) {
            ClientBender.get().tick();
            if (ClientBender.get().player != plr) {
                ClientBender.get().player = plr;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ClientBender bender = ClientBender.get();
        if ((bender.currAbility instanceof AbilityAirScooter && source.is(DamageTypes.FALL))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    


    @Inject(at = @At("RETURN"), method = "canStartSprinting", cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        ClientBender bender = ClientBender.get();
        Player plr = ((Player) (Object) this);
        
        if (bender.currAbility instanceof AbilityMetalDecoy) {
            cir.setReturnValue(!plr.isSprinting());
            cir.cancel();
        }
    }
    
}