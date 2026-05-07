package dev.saperate.elementals.network.packets.S2C;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncCurrAbilityPacket(int abilityIndex) {
    public static final StreamCodec<FriendlyByteBuf, SyncCurrAbilityPacket> STREAM_CODEC = StreamCodec.ofMember(SyncCurrAbilityPacket::encode, SyncCurrAbilityPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ModMessages.SYNC_CHI_PACKET_ID);
    }

    public SyncCurrAbilityPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(abilityIndex);
    }

    public static void handle(PacketContext<SyncCurrAbilityPacket> ctx)
    {
        ModMessages.expectSideOrThrow(ctx.side(), Side.CLIENT);

        int abilityIndex = ctx.message().abilityIndex;
        ClientBender bender = ClientBender.get();
        if(bender.getElement() == null){
            return;
        }
        if(abilityIndex == -1){
            bender.currAbility = null;
            return;
        }
        bender.currAbility = bender.getElement().getAbility(abilityIndex);
    }
}
