package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.network.payload.C2S.RequestSyncLevelPayload;
import dev.saperate.elementals.network.payload.S2C.SyncLevelPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncLevelC2SPacket {
    public static void receive(ServerPlayerEntity player, RequestSyncLevelPayload payload) {
        send(player);
    }

    public static void send(PlayerEntity player){
        PlayerData data = PlayerData.get(player);
        NbtCompound compound = new NbtCompound();

        compound.putInt("level",data.level);
        compound.putFloat("xp", data.xp);

        ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncLevelPayload(compound));
    }

}
