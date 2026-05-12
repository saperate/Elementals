package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.network.ElementalsNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AbilityPacket(int index, boolean isStart) {
    public static final StreamCodec<FriendlyByteBuf, AbilityPacket> STREAM_CODEC = StreamCodec.ofMember(AbilityPacket::encode, AbilityPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ElementalsNetworking.ABILITY_PACKET_ID);
    }

    public AbilityPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(index);
        buf.writeBoolean(isStart);
    }

    public static void handle(PacketContext<AbilityPacket> ctx)
    {
        ElementalsNetworking.expectSideOrThrow(ctx.side(), Side.SERVER);
        
        AbilityPacket packet = ctx.message();
        Bender.getBender(ctx.sender()).bend(packet.index, packet.isStart());
    }
}
