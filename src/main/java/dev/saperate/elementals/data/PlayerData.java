package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static dev.saperate.elementals.network.ModMessages.SYNC_CHI_PACKET_ID;

public class PlayerData {
    public Ability[] boundAbilities = new Ability[4];
    public Element element = Element.elementList.get(0);
    public HashMap<Upgrade, Boolean> upgrades = new HashMap<>();
    public float chi = 100, xp = 0;
    public int level = 0;


    /**
     * Checks if the player both has the upgrade AND has it enabled.
     * @param upgradeName The name of the upgrade we are checking
     * @return if the player can use the upgrade
     */
    public boolean canUseUpgrade(String upgradeName) {
        Upgrade key = new Upgrade(upgradeName);
        return upgrades.getOrDefault(key,false);
    }

    public boolean canBuyUpgrade(String upgradeName) {
        return canBuyUpgrade(upgrades,element,upgradeName);
    }

    public static boolean canBuyUpgrade(HashMap<Upgrade, Boolean> plrUpgrades, Element element, String upgradeName) {
        for (Upgrade branches : element.root.children) {
            for (Upgrade u : branches.nextUpgrades(plrUpgrades)) {
                if (u.name.equals(upgradeName) && u.canBuy(plrUpgrades)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static PlayerData get(LivingEntity player) {
        return StateDataSaverAndLoader.getPlayerState(player);
    }

}
