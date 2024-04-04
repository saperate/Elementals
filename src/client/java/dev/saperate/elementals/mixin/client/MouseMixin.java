package dev.saperate.elementals.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.saperate.elementals.network.ModMessages.MOUSE_PACKET_ID;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "onMouseButton")
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (this.client.currentScreen == null){
            boolean left = false, mid = false, right = false;
            if (button == 0) {
                left = action == 1;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                mid = action == 1;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                right = action == 1;
            }
            if(left || mid || right){
                PacketByteBuf packet = PacketByteBufs.create();
                packet.writeBoolean(left);
                packet.writeBoolean(mid);
                packet.writeBoolean(right);

                ClientPlayNetworking.send(MOUSE_PACKET_ID, packet);
            }

        }
    }
}
