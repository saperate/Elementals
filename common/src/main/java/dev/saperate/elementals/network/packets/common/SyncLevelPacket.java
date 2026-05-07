package dev.saperate.elementals.network.packets.common;

import commonnetwork.api.Network;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.network.ElementalsNetworking;
import dev.saperate.elementals.network.payload.S2C.SyncLevelPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record SyncLevelPacket(int level, float xp) {
    public static final StreamCodec<FriendlyByteBuf, SyncLevelPacket> STREAM_CODEC = StreamCodec.ofMember(SyncLevelPacket::encode, SyncLevelPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ElementalsNetworking.SYNC_LEVEL_PACKET_ID);
    }

    public SyncLevelPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(level);
        buf.writeFloat(xp);
    }

    public static void handle(PacketContext<SyncLevelPacket> ctx)
    {
        if(ctx.side().equals(Side.CLIENT)){
            SyncLevelPacket packet = ctx.message();
            
            ClientBender.get().level = packet.level;
            ClientBender.get().xp = packet.xp;
        }else{
            ServerPlayer player = ctx.sender();
            PlayerData data = PlayerData.get(player);

            Network.getNetworkHandler().sendToClient(new SyncLevelPacket(data.level, data.xp), player);
        }
    }
}
