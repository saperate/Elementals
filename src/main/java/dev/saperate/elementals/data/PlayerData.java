package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.network.packets.SyncLevelC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.saperate.elementals.network.ModMessages.SYNC_CHI_PACKET_ID;

public class PlayerData {
    public Ability[] boundAbilities = new Ability[4];
    public HashMap<Upgrade, Boolean> upgrades = new HashMap<>();
    public float chi = 100, xp = 0;
    public int level = 2;


    /**
     * Checks if the player both has the upgrade AND has it enabled.
     * @param upgradeName The name of the upgrade we are checking
     * @return if the player can use the upgrade
     */
    public boolean canUseUpgrade(String upgradeName) {
        Upgrade key = new Upgrade(upgradeName, 0);
        return upgrades.getOrDefault(key, false);
    }

    /**
     * This does not automatically sync the level of the player,
     * to do that, use the send method in SyncLevelC2SPacket.
     * @param upgrade The upgrade we want to buy
     * @return True if we were able to buy the upgrade, False if not
     * @see SyncLevelC2SPacket
     */
    public boolean buyUpgrade(Upgrade upgrade, Element element) {
        //Wrapper class so that we can reduce the levels
        AtomicInteger lvl = new AtomicInteger(level);

        boolean bought = canBuyUpgrade(upgrades, element, upgrade.name, lvl);
        if (bought) {
            upgrades.put(upgrade, true);
        }
        level = lvl.get();

        return bought;
    }

    public static boolean canBuyUpgrade(HashMap<Upgrade, Boolean> plrUpgrades, Element element, String upgradeName, AtomicInteger level) {
        for (Upgrade branches : element.root.children) {
            for (Upgrade upgrade : branches.nextUpgrades(plrUpgrades)) {
                if (upgrade.name.equals(upgradeName) && upgrade.canBuy(plrUpgrades)) {
                    if (level.get() >= upgrade.price) {
                        level.set(level.get() - upgrade.price);
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public static PlayerData get(LivingEntity player) {
        return StateDataSaverAndLoader.getPlayerState(player);
    }
}
