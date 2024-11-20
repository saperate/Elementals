package dev.saperate.elementals.keys.abilities;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
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
