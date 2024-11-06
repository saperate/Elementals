package dev.saperate.elementals.packets;

import dev.saperate.elementals.ElementalsClient;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.network.payload.SyncChiPayload;
import dev.saperate.elementals.network.payload.SyncCurrAbilityPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;

public class SyncChiS2CPacket implements ElementalsClient.ElementalPacket<SyncChiPayload>{

    @Override
    public void receive(MinecraftClient client, SyncChiPayload payload) {
        if (client.player == null) {
            return;
        }
        ClientBender.get().chi = payload.chi();
    }

    @Override
    public CustomPayload.Id<SyncChiPayload> getId() {
        return SyncChiPayload.ID;
    }
}
