package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility4 extends KeyInput {

    public KeyAbility4(){
        registerAbilityInput(
                GLFW.GLFW_KEY_C,
                3,
                "key.elementals.Ability4",
                "category.elementals"
        );
    }
}
