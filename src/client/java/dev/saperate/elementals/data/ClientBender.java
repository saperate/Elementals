package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientBender {
    private static ClientBender instance;
    public Element element;
    public Ability currAbility;
    public PlayerEntity player;
    public HashMap<Upgrade, Boolean> upgrades = new HashMap<>();

    private ClientBender() {

    }

    public static ClientBender get() {
        if (instance == null) {
            instance = new ClientBender();
        }
        return instance;
    }

}
