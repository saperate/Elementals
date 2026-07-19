package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility9 extends KeyInput {
    public KeyAbility9(){
        registerAbilityInput(
                GLFW.GLFW_KEY_J,
                8,
                "key.elementals.Ability9",
                "category.elementals"
        );
    }
}