package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PlayerData {
    public Ability[] boundAbilities = new Ability[4];
    public Element element = Element.elementList.get(0);
    public HashMap<Upgrade, Boolean> upgrades = new HashMap<>();


    public boolean canUseUpgrade(String upgradeName) {
        Upgrade key = new Upgrade(upgradeName);
        if (upgrades.containsKey(key)) {
            return upgrades.get(key);
        }
        return false;
    }

    public static PlayerData get(LivingEntity player) {
        return StateDataSaverAndLoader.getPlayerState(player);
    }

}
