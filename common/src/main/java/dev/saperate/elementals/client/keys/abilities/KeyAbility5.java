package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility5 extends KeyInput {
    public KeyAbility5(){
        registerAbilityInput(
                GLFW.GLFW_KEY_B,
                4,
                "key.elementals.Ability5",
                "category.elementals"
        );
    }
}