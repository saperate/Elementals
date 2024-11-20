package dev.saperate.elementals.network.payload.S2C;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncChiPayload(float chi) implements CustomPayload {
    public static final CustomPayload.Id<SyncChiPayload> ID = new CustomPayload.Id<>(ModMessages.SYNC_CHI_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncChiPayload> CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, SyncChiPayload::chi, SyncChiPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
