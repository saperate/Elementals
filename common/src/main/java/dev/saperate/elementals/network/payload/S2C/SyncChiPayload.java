package dev.saperate.elementals.network.payload.S2C;

import commonnetwork.networking.data.PacketContext;
import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncChiPayload(float chi) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncChiPayload> ID = new CustomPacketPayload.Type<>(ModMessages.SYNC_CHI_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncChiPayload> CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, SyncChiPayload::chi, SyncChiPayload::new);
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
    
    public static void handle(PacketContext<CustomPacketPayload> ctx)
    {
        
    }
}
