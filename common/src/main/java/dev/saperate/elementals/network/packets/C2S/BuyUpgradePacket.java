package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.network.ModMessages;
import dev.saperate.elementals.network.packets.GetUpgradeListC2SPacket;
import dev.saperate.elementals.network.packets.SyncLevelC2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record BuyUpgradePacket(String name) {
    public static final StreamCodec<FriendlyByteBuf, BuyUpgradePacket> STREAM_CODEC = StreamCodec.ofMember(BuyUpgradePacket::encode, BuyUpgradePacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ModMessages.BUY_UPGRADE_PACKET_ID);
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
        ModMessages.isOnSideOrThrow(ctx.side(), Side.CLIENT);
        
        String name = ctx.message().name();
        ServerPlayer player = ctx.sender();
        Bender bender = Bender.getBender(player);

        if (name.startsWith("bending")) {
            bender.addElement(Element.getElement(name.replace("bending", "")), true);
            bender.bindDefaultAbilities();
            StateDataSaverAndLoader.getServerState(player.server).setChanged();
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
            GetUpgradeListC2SPacket.send(player);
            SyncLevelC2SPacket.send(player);
        }
    }
}
