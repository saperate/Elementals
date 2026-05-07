package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.network.ModMessages;
import dev.saperate.elementals.network.packets.GetUpgradeListC2SPacket;
import dev.saperate.elementals.network.packets.SyncLevelC2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ToggleUpgradePacket(String name) {
    public static final StreamCodec<FriendlyByteBuf, ToggleUpgradePacket> STREAM_CODEC = StreamCodec.ofMember(ToggleUpgradePacket::encode, ToggleUpgradePacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ModMessages.TOGGLE_UPGRADE_PACKET_ID);
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
        ModMessages.expectSideOrThrow(ctx.side(), Side.SERVER);
        
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
        GetUpgradeListC2SPacket.send(player);
        SyncLevelC2SPacket.send(player);
    }
}
