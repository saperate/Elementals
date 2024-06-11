package dev.saperate.elementals.packets;

import dev.saperate.elementals.data.ClientBender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import static dev.saperate.elementals.network.ModMessages.GET_UPGRADE_LIST_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_LEVEL_PACKET_ID;

public class SyncLevelS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        if (client.player == null) {
            return;
        }
        ClientBender.get().level = buf.readInt();
        ClientBender.get().xp = buf.readFloat();
    }

    public static void send(){
        ClientPlayNetworking.send(SYNC_LEVEL_PACKET_ID, PacketByteBufs.create());
    }
}
