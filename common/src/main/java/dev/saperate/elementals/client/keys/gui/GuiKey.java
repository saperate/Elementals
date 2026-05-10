package dev.saperate.elementals.client.keys.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.saperate.elementals.client.gui.UpgradeTreeScreen;
import dev.saperate.elementals.client.keys.KeyInput;
import dev.saperate.elementals.platform.Services;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class GuiKey extends KeyInput {
    private final KeyMapping keyBinding;
    public boolean lastFrameWasHolding;

    public GuiKey() {
        keyBinding = Services.REGISTRY.registerKeyBinding(new KeyMapping(
                "key.elementals.guiKey",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "category.elementals"
        ));
        Services.EVENTS.onClientTick(client -> {
            if (keyBinding.isDown() && !lastFrameWasHolding) {
                lastFrameWasHolding = true;
            }
            if (!keyBinding.isDown() && lastFrameWasHolding) {
                lastFrameWasHolding = false;
                client.setScreen(new UpgradeTreeScreen(null));
            }
        });
    }


}
