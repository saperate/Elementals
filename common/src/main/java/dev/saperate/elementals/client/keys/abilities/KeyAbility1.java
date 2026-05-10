package dev.saperate.elementals.client.keys.abilities;

import dev.saperate.elementals.client.keys.KeyInput;
import org.lwjgl.glfw.GLFW;

public class KeyAbility1 extends KeyInput {

    public KeyAbility1(){
        registerAbilityInput(
                GLFW.GLFW_KEY_R,
                0,
                "key.elementals.Ability1",
                "category.elementals"
        );
    }

}
