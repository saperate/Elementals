package dev.saperate.elementals.network.packets.common;

import commonnetwork.api.Network;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record SyncUpgradeListPacket(CompoundTag data) {
    public static final StreamCodec<FriendlyByteBuf, SyncUpgradeListPacket> STREAM_CODEC = StreamCodec.ofMember(SyncUpgradeListPacket::encode, SyncUpgradeListPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ModMessages.SYNC_UPGRADE_LIST_PACKET_ID);
    }

    public SyncUpgradeListPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeNbt(data);
    }

    public static void handle(PacketContext<SyncUpgradeListPacket> ctx)
    {
        if(ctx.side().equals(Side.CLIENT)){
            SyncUpgradeListPacket packet = ctx.message();

            ClientBender bender = ClientBender.get();
            if(bender.getElement() == null){
                return;
            }
            bender.upgrades.clear();
            CompoundTag data = packet.data();
            bender.getElement().onRead(data,bender.upgrades);
        }else{
            ServerPlayer player = ctx.sender();
            Bender bender = Bender.getBender(player);
            Network.getNetworkHandler().sendToClient(
                    new SyncUpgradeListPacket
                            (bender.getElement().onSave(PlayerData.get(player).upgrades)), 
                    player);
        }
    }
}
