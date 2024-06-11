package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_LEVEL_PACKET_ID;

public class SyncLevelC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        // Everything here happens ONLY on the Server!
        server.execute(() -> {
            send(player);
        });
    }

    public static void send(PlayerEntity player){
        PlayerData data = PlayerData.get(player);
        PacketByteBuf out = PacketByteBufs.create();
        out.writeInt(data.level);
        out.writeFloat(data.xp);
        ServerPlayNetworking.send((ServerPlayerEntity) player, SYNC_LEVEL_PACKET_ID, out);
    }

}
