package dev.saperate.elementals.client.keys;

import com.mojang.blaze3d.platform.InputConstants;
import commonnetwork.api.Network;
import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.network.packets.C2S.AbilityPacket;
import dev.saperate.elementals.platform.Services;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyInput {
    public static final List<KeyInput> keyInputs = new ArrayList<>();
    public static final List<KeyMapping> bindings = new ArrayList<>();
    public KeyMapping keyBinding;
    public boolean lastFrameWasHolding;

    public KeyInput() {
        keyInputs.add(this);
    }

    public void registerAbilityInput(int GLFWKey, int abilityIndex, String translationKey, String category) {
        keyBinding = Services.REGISTRY.registerClientKeyBinding(new KeyMapping(
                translationKey,
                InputConstants.Type.KEYSYM,
                GLFWKey,
                category
        ));
        bindings.add(keyBinding);
        
        Services.EVENTS.onClientTick(client -> {
            if (keyBinding.isDown() && !lastFrameWasHolding && !ClientBender.get().isCasting()) {
                ClientBender.get().startCasting();
                lastFrameWasHolding = true;
                onStartHolding(abilityIndex);
            }
            if (!keyBinding.isDown() && lastFrameWasHolding) {
                ClientBender.get().stopCasting();
                lastFrameWasHolding = false;
                onEndHolding(abilityIndex);
            }
        });
    }


    public void onStartHolding(int abilityIndex) {
        Network.getNetworkHandler().sendToServer(new AbilityPacket(abilityIndex, true));
    }

    public void onEndHolding(int abilityIndex) {
        Network.getNetworkHandler().sendToServer(new AbilityPacket(abilityIndex, false));
    }

}
