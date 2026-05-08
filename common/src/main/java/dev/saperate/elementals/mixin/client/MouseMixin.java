package dev.saperate.elementals.mixin.client;

import commonnetwork.api.Network;
import dev.saperate.elementals.network.packets.C2S.MouseClickPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.nbt.CompoundTag;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(at = @At("HEAD"), method = "onPress")
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (this.minecraft.screen == null) {
            int left = -1, mid = -1, right = -1;
            if (button == 0) {
                left = action;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                mid = action;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                right = action;
            }
            CompoundTag data = new CompoundTag();
            data.putInt("left", left);
            data.putInt("middle", mid);
            data.putInt("right", right);

            Network.getNetworkHandler().sendToServer(new MouseClickPacket(data));
        }
    }
}
