package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility8 extends KeyInput {
    public KeyAbility8(){
        registerAbilityInput(
                GLFW.GLFW_KEY_H,
                7,
                "key.elementals.Ability8",
                "category.elementals"
        );
    }
}