package dev.saperate.elementals.keys.abilities;

import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.network.ModMessages;
import dev.saperate.elementals.network.packets.Ability1C2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyAbility1 extends KeyInput {

    public KeyAbility1(){
        registerKeyInput(
                GLFW.GLFW_KEY_R,
                ModMessages.ABILITY1_PACKET_ID,
                "key.elementals.Ability1",
                "category.elementals"
        );
    }

}
