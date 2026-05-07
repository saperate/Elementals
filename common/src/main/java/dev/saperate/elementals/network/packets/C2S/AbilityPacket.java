package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AbilityPacket(int index, boolean isStart) {
    public static final StreamCodec<FriendlyByteBuf, AbilityPacket> STREAM_CODEC = StreamCodec.ofMember(AbilityPacket::encode, AbilityPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ModMessages.ABILITY_PACKET_ID);
    }

    public AbilityPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeFloat(index);
        buf.writeBoolean(isStart);
    }

    public static void handle(PacketContext<AbilityPacket> ctx)
    {
        ModMessages.isOnSideOrThrow(ctx.side(), Side.SERVER);
        
        AbilityPacket packet = ctx.message();
        Bender.getBender(ctx.sender()).bend(packet.index, packet.isStart());
    }
}
