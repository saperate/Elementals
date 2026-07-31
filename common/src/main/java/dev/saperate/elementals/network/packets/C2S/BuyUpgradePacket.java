package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.api.Network;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.network.ElementalsNetworking;
import dev.saperate.elementals.network.packets.common.SyncLevelPacket;
import dev.saperate.elementals.network.packets.common.SyncUpgradeListPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record BuyUpgradePacket(String name) {
    public static final StreamCodec<FriendlyByteBuf, BuyUpgradePacket> STREAM_CODEC = StreamCodec.ofMember(BuyUpgradePacket::encode, BuyUpgradePacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ElementalsNetworking.BUY_UPGRADE_PACKET_ID);
    }

    public BuyUpgradePacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(name);
    }

    public static void handle(PacketContext<BuyUpgradePacket> ctx)
    {
        ElementalsNetworking.expectSideOrThrow(ctx.side(), Side.SERVER);
        
        String name = ctx.message().name();
        ServerPlayer player = ctx.sender();
        Bender bender = Bender.getBender(player);

        if (name.startsWith("bending")) {
            bender.addElement(Element.getElement(name.replace("bending", "")), true);
            StateDataSaverAndLoader.getServerState(player.server).setDirty();
            return;
        }

        Upgrade upgrade = bender.getElement().root.getUpgradeByNameRecursive(name);
        if (upgrade == null) {
            return;
        }


        PlayerData plrData = PlayerData.get(player);
        if (plrData.buyUpgrade(upgrade)) {
            if(upgrade.parent.exclusive){
                //Will disable sister upgrades
                plrData.setUpgrade(upgrade,true);
            }
            Network.getNetworkHandler().sendToClient(SyncUpgradeListPacket.createFromBender(bender), player);
            Network.getNetworkHandler().sendToClient(SyncLevelPacket.createFromBender(bender), player);
        }
    }
}
