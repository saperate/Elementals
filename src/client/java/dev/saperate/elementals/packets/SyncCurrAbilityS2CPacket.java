package dev.saperate.elementals.packets;

import dev.saperate.elementals.ElementalsClient;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.network.payload.SyncCurrAbilityPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;

public class SyncCurrAbilityS2CPacket implements ElementalsClient.ElementalPacket<SyncCurrAbilityPayload> {
    @Override
    public void receive(MinecraftClient client, SyncCurrAbilityPayload payload) {
        if(client.player == null){
            return;
        }
        int i = payload.abilityIndex();
        ClientBender bender = ClientBender.get();
        if(bender.getElement() == null){
            return;
        }
        if(i == -1){
            bender.currAbility = null;
            return;
        }
        bender.currAbility = bender.getElement().getAbility(i);
    }

    @Override
    public CustomPayload.Id<SyncCurrAbilityPayload> getId() {
        return SyncCurrAbilityPayload.ID;
    }
}
