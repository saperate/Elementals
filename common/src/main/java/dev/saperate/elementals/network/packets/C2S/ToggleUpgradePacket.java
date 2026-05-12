package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.api.Network;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.network.ElementalsNetworking;
import dev.saperate.elementals.network.packets.common.SyncLevelPacket;
import dev.saperate.elementals.network.packets.common.SyncUpgradeListPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ToggleUpgradePacket(String name) {
    public static final StreamCodec<FriendlyByteBuf, ToggleUpgradePacket> STREAM_CODEC = StreamCodec.ofMember(ToggleUpgradePacket::encode, ToggleUpgradePacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ElementalsNetworking.TOGGLE_UPGRADE_PACKET_ID);
    }

    public ToggleUpgradePacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(name);
    }

    public static void handle(PacketContext<ToggleUpgradePacket> ctx)
    {
        ElementalsNetworking.expectSideOrThrow(ctx.side(), Side.SERVER);
        
        String name = ctx.message().name();
        ServerPlayer player = ctx.sender();
        Bender bender = Bender.getBender(player);

        if (name.startsWith("bending")) {
            return;
        }

        Upgrade upgrade = bender.getElement().root.getUpgradeByNameRecursive(name);
        if (upgrade == null) {
            return;
        }

        PlayerData plrData = PlayerData.get(player);
        plrData.toggleUpgrade(upgrade);
        Network.getNetworkHandler().sendToClient(SyncUpgradeListPacket.createFromBender(bender), player);
        Network.getNetworkHandler().sendToClient(SyncLevelPacket.createFromBender(bender), player);
    }
}
