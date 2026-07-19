package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility7 extends KeyInput {
    public KeyAbility7(){
        registerAbilityInput(
                GLFW.GLFW_KEY_U,
                4,
                "key.elementals.Ability7",
                "category.elementals"
        );
    }
}