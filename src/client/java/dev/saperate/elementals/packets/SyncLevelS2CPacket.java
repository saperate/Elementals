package dev.saperate.elementals.packets;

import dev.saperate.elementals.ElementalsClient;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.network.payload.C2S.RequestSyncLevelPayload;
import dev.saperate.elementals.network.payload.S2C.SyncCurrAbilityPayload;
import dev.saperate.elementals.network.payload.S2C.SyncLevelPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.GET_UPGRADE_LIST_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_LEVEL_PACKET_ID;

public class SyncLevelS2CPacket implements ElementalsClient.ElementalPacket<SyncLevelPayload>  {
    @Override
    public void receive(MinecraftClient client, SyncLevelPayload payload) {
        if (client.player == null) {
            return;
        }
        ClientBender.get().level = payload.getLevel();
        ClientBender.get().xp = payload.getXP();
    }

    @Override
    public CustomPayload.Id<SyncLevelPayload> getId() {
        return SyncLevelPayload.ID;
    }

    public static void send(){
        ClientPlayNetworking.send(new RequestSyncLevelPayload(-1));
    }
}
