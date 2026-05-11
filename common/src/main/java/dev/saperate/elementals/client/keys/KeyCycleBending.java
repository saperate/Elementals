package dev.saperate.elementals.client.keys;

import com.mojang.blaze3d.platform.InputConstants;
import commonnetwork.api.Network;
import dev.saperate.elementals.network.packets.C2S.CycleBendingPacket;
import dev.saperate.elementals.platform.Services;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;


public class KeyCycleBending extends KeyInput {
    private final KeyMapping keyBinding;
    public boolean lastFrameWasHolding;

    public KeyCycleBending() {
        keyBinding = Services.REGISTRY.registerClientKeyBinding(new KeyMapping(
                "key.elementals.cycle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.elementals"
        ));
        
        Services.EVENTS.onClientTick(client -> {
            if (keyBinding.isDown() && !lastFrameWasHolding) {
                lastFrameWasHolding = true;
                boolean back = client.player != null && client.player.isCrouching();
                Network.getNetworkHandler().sendToServer(new CycleBendingPacket(back));
            } else if (!keyBinding.isDown() && lastFrameWasHolding) {
                lastFrameWasHolding = false;
            }
        });
    }
}
