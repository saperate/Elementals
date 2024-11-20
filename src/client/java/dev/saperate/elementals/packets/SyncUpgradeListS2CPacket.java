package dev.saperate.elementals.packets;

import dev.saperate.elementals.ElementalsClient;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.network.payload.C2S.RequestSyncLevelPayload;
import dev.saperate.elementals.network.payload.C2S.RequestSyncUpgradeListPayload;
import dev.saperate.elementals.network.payload.S2C.SyncLevelPayload;
import dev.saperate.elementals.network.payload.S2C.SyncUpgradeListPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.GET_UPGRADE_LIST_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;

public class SyncUpgradeListS2CPacket implements ElementalsClient.ElementalPacket<SyncUpgradeListPayload>  {
    @Override
    public void receive(MinecraftClient client, SyncUpgradeListPayload payload) {
        if(client.player == null){
            return;
        }
        ClientBender bender = ClientBender.get();
        if(bender.getElement() == null){
            return;
        }
        bender.upgrades.clear();
        NbtCompound data = payload.data();
        bender.getElement().onRead(data,bender.upgrades);
    }

    @Override
    public CustomPayload.Id<SyncUpgradeListPayload> getId() {
        return SyncUpgradeListPayload.ID;
    }

    public static void send(){
        ClientPlayNetworking.send(new RequestSyncUpgradeListPayload(true));
    }

}
