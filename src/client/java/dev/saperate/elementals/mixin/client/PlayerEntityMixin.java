package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {


    @Inject(at = @At("TAIL"), method = "tick")
    private void render(CallbackInfo ci) {
        PlayerEntity plr = ((PlayerEntity) (Object) this);
        if (plr == MinecraftClient.getInstance().player) {
            if(ClientBender.get().player != plr){
                ClientBender.get().player = plr;
            }
            ClientBender.get().tick();
        }
    }
}