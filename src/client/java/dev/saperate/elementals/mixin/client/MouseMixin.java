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
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "onMouseButton")
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (this.client.currentScreen == null) {
            int left = -1, mid = -1, right = -1;
            if (button == 0) {
                left = action;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                mid = action;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                right = action;
            }
            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeInt(left);
            packet.writeInt(mid);
            packet.writeInt(right);

            ClientPlayNetworking.send(MOUSE_PACKET_ID, packet);
        }
    }
}
