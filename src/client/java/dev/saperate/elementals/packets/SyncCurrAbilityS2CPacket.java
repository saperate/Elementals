package dev.saperate.elementals.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.Element;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;

public class SyncCurrAbilityS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        if(client.player == null){
            return;
        }
        int i = buf.readInt();
        ClientBender bender = ClientBender.get();
        if(bender.element == null){
            return;
        }
        if(i == -1){
            bender.currAbility = null;
            return;
        }
        bender.currAbility = bender.element.getAbility(i);
    }

    public static void send(Bender bender, int i){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(i);
        ServerPlayNetworking.send((ServerPlayerEntity) bender.player,SYNC_CURR_ABILITY_PACKET_ID,buf);
    }
}
