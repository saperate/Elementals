package dev.saperate.elementals.keys.abilities;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
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
