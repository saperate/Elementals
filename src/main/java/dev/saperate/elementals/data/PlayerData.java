package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public Ability[] boundAbilities = new Ability[4];
    public Element element = Element.elementList.get(0);
    public List<Upgrade> boughtUpgrades = new ArrayList<>();
    public List<Upgrade> activeUpgrades = new ArrayList<>();


    public boolean canUseUpgrade(String upgradeName) {
        for (Upgrade upgrade : activeUpgrades) {
            if (upgrade.name.equals(upgradeName)) {
                return true;
            }
        }
        return true;//TODO change this back to false
    }

    public static PlayerData get(LivingEntity player) {
        return StateDataSaverAndLoader.getPlayerState(player);
    }

}
