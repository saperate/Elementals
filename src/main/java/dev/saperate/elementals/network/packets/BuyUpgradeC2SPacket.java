package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.network.payload.C2S.BuyUpgradePayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_UPGRADE_LIST_PACKET_ID;

public class BuyUpgradeC2SPacket {
    public static void receive(ServerPlayerEntity player, BuyUpgradePayload payload) {
        // Everything here happens ONLY on the Server!
        String name = payload.name();
        Bender bender = Bender.getBender(player);

        if (name.startsWith("bending")) {
            bender.addElement(Element.getElementByName(name.replace("bending", "")), true);
            bender.bindDefaultAbilities();
            StateDataSaverAndLoader.getServerState(player.server).markDirty();
            return;
        }

        Upgrade upgrade = bender.getElement().root.getUpgradeByNameRecursive(name);
        if (upgrade == null) {
            return;
        }

        PlayerData plrData = PlayerData.get(player);
        if (plrData.buyUpgrade(upgrade)) {
            GetUpgradeListC2SPacket.send(player);
            SyncLevelC2SPacket.send(player);
        }
    }

}
