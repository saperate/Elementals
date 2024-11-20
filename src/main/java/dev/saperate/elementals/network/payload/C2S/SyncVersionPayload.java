package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncVersionPayload(String version) implements CustomPayload {
    public static final Id<SyncVersionPayload> ID = new Id<>(ModMessages.GET_MOD_VERSION_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncVersionPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SyncVersionPayload::version, SyncVersionPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
