package dev.saperate.elementals.keys;

import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.network.payload.C2S.AbilityPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyInput {
    public static final List<KeyInput> keyInputs = new ArrayList<>();
    public static final List<KeyBinding> bindings = new ArrayList<>();
    public KeyBinding keyBinding;
    public boolean lastFrameWasHolding;

    public KeyInput() {
        keyInputs.add(this);
    }

    public void registerAbilityInput(int GLFWKey, int abilityIndex, String translationKey, String category) {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey,
                InputUtil.Type.KEYSYM,
                GLFWKey,
                category
        ));
        bindings.add(keyBinding);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBinding.isPressed() && !lastFrameWasHolding && !ClientBender.get().isCasting()) {
                ClientBender.get().startCasting();
                lastFrameWasHolding = true;
                onStartHolding(abilityIndex);
            }
            if (!keyBinding.isPressed() && lastFrameWasHolding) {
                ClientBender.get().stopCasting();
                lastFrameWasHolding = false;
                onEndHolding(abilityIndex);
            }
        });
    }


    public void onStartHolding(int abilityIndex) {
        NbtCompound data = new NbtCompound();

        data.putInt("index", abilityIndex);
        data.putBoolean("isStart", true);

        ClientPlayNetworking.send(new AbilityPayload(data));


    }

    public void onEndHolding(int abilityIndex) {
        NbtCompound data = new NbtCompound();

        data.putInt("index", abilityIndex);
        data.putBoolean("isStart", false);

        ClientPlayNetworking.send(new AbilityPayload(data));
    }

}
