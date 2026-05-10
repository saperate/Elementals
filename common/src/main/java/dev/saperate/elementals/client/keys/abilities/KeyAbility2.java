package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility2 extends KeyInput {

    public KeyAbility2(){
        registerAbilityInput(
                GLFW.GLFW_KEY_G,
                1,
                "key.elementals.Ability2",
                "category.elementals"
        );
    }
}
