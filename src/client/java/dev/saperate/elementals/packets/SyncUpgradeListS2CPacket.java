package dev.saperate.elementals.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.GET_UPGRADE_LIST_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;

public class SyncUpgradeListS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        if(client.player == null){
            return;
        }
        ClientBender bender = ClientBender.get();
        if(bender.element == null){
            return;
        }
        NbtCompound data = buf.readNbt();
        bender.element.onRead(data,bender.upgrades);
    }

    public static void send(){
        ClientPlayNetworking.send(GET_UPGRADE_LIST_PACKET_ID, PacketByteBufs.create());
    }
}
