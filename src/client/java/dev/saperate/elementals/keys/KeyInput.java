package dev.saperate.elementals.keys;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyInput {
    private static final List<KeyInput> keyInputs = new ArrayList<>();
    private static KeyBinding keyBinding;

    public KeyInput(){
        keyInputs.add(this);
    }

    public void registerKeyInput(int GLFWKey, Identifier packetID, String translationKey, String category){
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey,
                InputUtil.Type.KEYSYM,
                GLFWKey,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                onPressed(packetID);
            }
        });
    }

    public void onPressed(Identifier packetID){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ClientPlayNetworking.send(packetID, PacketByteBufs.create());
    }
}
