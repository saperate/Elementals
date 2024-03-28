package dev.saperate.elementals.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_ELEMENT_PACKET_ID;

public class SyncBendingElementS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        if(client.player == null){
            return;
        }
        String e = buf.readString();
        Bender.getBender(client.player).setElement(Element.getElementByName(e));

    }
}
