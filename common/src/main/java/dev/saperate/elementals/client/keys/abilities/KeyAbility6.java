package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility6 extends KeyInput {
    public KeyAbility6(){
        registerAbilityInput(
                GLFW.GLFW_KEY_Y,
                5,
                "key.elementals.Ability6",
                "category.elementals"
        );
    }
}