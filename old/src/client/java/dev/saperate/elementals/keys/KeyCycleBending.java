package dev.saperate.elementals.keys;

import dev.saperate.elementals.gui.UpgradeTreeScreen;
import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
import dev.saperate.elementals.network.payload.C2S.AbilityPayload;
import dev.saperate.elementals.network.payload.C2S.CycleBendingPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyMapping;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static dev.saperate.elementals.network.ModMessages.CYCLE_BENDING_PACKET_ID;

public class KeyCycleBending extends KeyInput {
    private final KeyMapping keyBinding;
    public boolean lastFrameWasHolding;

    public KeyCycleBending() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.elementals.cycle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.elementals"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBinding.isPressed() && !lastFrameWasHolding) {
                lastFrameWasHolding = true;
                ClientPlayNetworking.send(new CycleBendingPayload(false));
            } else if (!keyBinding.isPressed() && lastFrameWasHolding) {
                lastFrameWasHolding = false;
            }
        });
    }
}
