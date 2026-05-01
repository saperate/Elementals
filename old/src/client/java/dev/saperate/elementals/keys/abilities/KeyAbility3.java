package dev.saperate.elementals.keys.abilities;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
import org.lwjgl.glfw.GLFW;

public class KeyAbility3 extends KeyInput {

    public KeyAbility3(){
        registerAbilityInput(
                GLFW.GLFW_KEY_V,
                2,
                "key.elementals.Ability3",
                "category.elementals"
        );
    }
}
