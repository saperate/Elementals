package dev.saperate.elementals.network.packets.S2C;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncChiPacket(float chi) {
    public static final StreamCodec<FriendlyByteBuf, SyncChiPacket> STREAM_CODEC = StreamCodec.ofMember(SyncChiPacket::encode, SyncChiPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ModMessages.SYNC_CHI_PACKET_ID);
    }

    public SyncChiPacket(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeFloat(chi);
    }

    public static void handle(PacketContext<SyncChiPacket> ctx)
    {
        ModMessages.isOnSideOrThrow(ctx.side(), Side.CLIENT);
        
    }
}
