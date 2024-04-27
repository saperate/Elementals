package dev.saperate.elementals.keys.abilities;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
import org.lwjgl.glfw.GLFW;

public class KeyAbility4 extends KeyInput {

    public KeyAbility4(){
        registerKeyInput(
                GLFW.GLFW_KEY_B,
                ModMessages.ABILITY4_PACKET_ID,
                "key.elementals.Ability4",
                "category.elementals"
        );
    }
}
