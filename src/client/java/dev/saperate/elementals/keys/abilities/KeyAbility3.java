package dev.saperate.elementals.keys.abilities;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
import org.lwjgl.glfw.GLFW;

public class KeyAbility3 extends KeyInput {

    public KeyAbility3(){
        registerKeyInput(
                GLFW.GLFW_KEY_V,
                ModMessages.ABILITY3_PACKET_ID,
                "key.elementals.Ability3",
                "category.elementals"
        );
    }
}
