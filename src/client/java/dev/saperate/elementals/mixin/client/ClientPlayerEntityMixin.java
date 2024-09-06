package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.air.AbilityAirScooter;
import dev.saperate.elementals.items.ElementalItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow @Final protected MinecraftClient client;


    @Inject(at = @At("TAIL"), method = "tick")
    private void render(CallbackInfo ci) {
        PlayerEntity plr = ((PlayerEntity) (Object) this);
        if (plr == MinecraftClient.getInstance().player) {
            ClientBender.get().tick();
            if (ClientBender.get().player != plr) {
                ClientBender.get().player = plr;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ClientBender bender = ClientBender.get();
        if ((bender.currAbility instanceof AbilityAirScooter && source.isOf(DamageTypes.FALL))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}