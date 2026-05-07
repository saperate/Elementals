package dev.saperate.elementals.network.packets.S2C;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.network.ElementalsNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncElementsPacket(String packedElements, int activeElementIndex) {
    public static final StreamCodec<FriendlyByteBuf, SyncElementsPacket> STREAM_CODEC = StreamCodec.ofMember(SyncElementsPacket::encode, SyncElementsPacket::new);
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ElementalsNetworking.SYNC_ELEMENT_PACKET_ID);
    }
    
    public SyncElementsPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(),buf.readInt());
    }

    //TODO write a more robust encoder/decoder
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(packedElements);
        buf.writeInt(activeElementIndex);
    }

    public static void handle(PacketContext<SyncElementsPacket> ctx)
    {
        ElementalsNetworking.expectSideOrThrow(ctx.side(), Side.CLIENT);
        ClientBender bender = ClientBender.get();
        SyncElementsPacket packet = ctx.message();
        
        String elements = packet.packedElements();
        int activeElementIndex = packet.activeElementIndex();
        
        bender.setElements(Bender.unpackElementsFromString(elements));
        bender.setActiveElementIndex(activeElementIndex);
        if (Minecraft.getInstance().screen instanceof UpgradeTreeScreen treeScreen) {
            treeScreen.close();
        }
        if(bender.chi > 100){
            bender.chi = 100;
        }
    }
}
