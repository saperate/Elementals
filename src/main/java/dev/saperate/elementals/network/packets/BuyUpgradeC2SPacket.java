package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Upgrade;
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
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        // Everything here happens ONLY on the Server!
        server.execute(() -> {
            Bender bender = Bender.getBender(player);

            Upgrade upgrade = bender.getElement().root.getUpgradeByNameRecursive(buf.readString());
            if (upgrade == null) {
                return;
            }

            PlayerData plrData = PlayerData.get(player);
            if (plrData.canBuyUpgrade(upgrade.name)) {
                plrData.upgrades.put(upgrade, true);
                GetUpgradeListC2SPacket.send(player);
            }
        });
    }

}
