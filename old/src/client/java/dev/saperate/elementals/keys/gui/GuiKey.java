package dev.saperate.elementals.keys.gui;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.gui.UpgradeTreeScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyMapping;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GuiKey extends KeyInput {
    private final KeyMapping keyBinding;
    public boolean lastFrameWasHolding;

    public GuiKey() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.elementals.guiKey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "category.elementals"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBinding.isPressed() && !lastFrameWasHolding) {
                lastFrameWasHolding = true;
            }
            if (!keyBinding.isPressed() && lastFrameWasHolding) {
                lastFrameWasHolding = false;
                MinecraftClient.getInstance().setScreen(new UpgradeTreeScreen(null));
            }
        });
    }


}
