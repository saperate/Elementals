package dev.saperate.elementals.keys.abilities;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
import org.lwjgl.glfw.GLFW;

public class KeyAbility2 extends KeyInput {

    public KeyAbility2(){
        registerKeyInput(
                GLFW.GLFW_KEY_G,
                ModMessages.ABILITY2_PACKET_ID,
                "key.elementals.Ability2",
                "category.elementals"
        );
    }
}
