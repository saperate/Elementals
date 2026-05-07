package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CycleBendingPacket(boolean data) {
    public static final StreamCodec<FriendlyByteBuf, CycleBendingPacket> STREAM_CODEC = StreamCodec.ofMember(CycleBendingPacket::encode, CycleBendingPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ModMessages.CYCLE_BENDING_PACKET_ID);
    }

    public CycleBendingPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBoolean(data);
    }

    public static void handle(PacketContext<CycleBendingPacket> ctx)
    {
        ModMessages.isOnSideOrThrow(ctx.side(), Side.SERVER);
        
        CycleBendingPacket packet = ctx.message();
        Bender bender = Bender.getBender(ctx.sender());
        PlayerData data = bender.getData();

        int nextIndex = data.activeElementIndex + (ctx.sender().isCrouching() ? -1 : 1);
        if(nextIndex > data.elements.size() - 1){
            nextIndex = 0;
        } else if (nextIndex < 0) {
            nextIndex = data.elements.size() - 1;
        }

        bender.setElement(nextIndex, true);
    }
}
