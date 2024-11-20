package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import dev.saperate.elementals.network.payload.S2C.SyncLevelPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RequestSyncLevelPayload (int level) implements CustomPayload {
    public static final Id<RequestSyncLevelPayload> ID = new Id<>(ModMessages.SYNC_LEVEL_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, RequestSyncLevelPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, RequestSyncLevelPayload::level, RequestSyncLevelPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
