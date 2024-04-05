package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import net.minecraft.entity.player.PlayerEntity;

public class ClientBender {
    private static ClientBender instance;

    public Element element;
    public Ability currAbility;
    public PlayerEntity player;

    private ClientBender() {

    }

    public static ClientBender get() {
        if (instance == null) {
            instance = new ClientBender();
        }
        return instance;
    }

}
