package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.network.payload.C2S.RequestSyncUpgradeListPayload;
import dev.saperate.elementals.network.payload.S2C.SyncLevelPayload;
import dev.saperate.elementals.network.payload.S2C.SyncUpgradeListPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_UPGRADE_LIST_PACKET_ID;

public class GetUpgradeListC2SPacket {
    public static void receive(PlayerEntity player, RequestSyncUpgradeListPayload payload) {
        // Everything here happens ONLY on the Server!
        send(player);
    }

    public static void send(PlayerEntity player){
        Bender bender = Bender.getBender((ServerPlayerEntity) player);
        NbtCompound data = bender.getElement().onSave(PlayerData.get(player).upgrades);
        ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncUpgradeListPayload(data));
    }

}
