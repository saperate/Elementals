package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility10 extends KeyInput {
    public KeyAbility10(){
        registerAbilityInput(
                GLFW.GLFW_KEY_K,
                4,
                "key.elementals.Ability10",
                "category.elementals"
        );
    }
}