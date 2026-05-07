package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.network.ElementalsNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MouseClickPacket(CompoundTag data) {
    public static final StreamCodec<FriendlyByteBuf, MouseClickPacket> STREAM_CODEC = StreamCodec.ofMember(MouseClickPacket::encode, MouseClickPacket::new);
    
    
    public static CustomPacketPayload.Type<CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(ElementalsNetworking.MOUSE_PACKET_ID);
    }

    public MouseClickPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeNbt(data);
    }

    public int getLeft(){
        return data.getInt("left");
    }
    public int getMiddle(){
        return data.getInt("middle");
    }
    public int getRight(){
        return data.getInt("right");
    }

    public static void handle(PacketContext<MouseClickPacket> ctx)
    {
        ElementalsNetworking.expectSideOrThrow(ctx.side(), Side.SERVER);
        
        MouseClickPacket packet = ctx.message();
        int left = packet.getLeft();
        int mid = packet.getMiddle();
        int right = packet.getRight();


        Bender bender = Bender.getBender(ctx.sender());
        if (left != -1) {
            if (bender.currAbility != null && bender.castTime == null) {
                bender.currAbility.onLeftClick(bender, left == 1);
            }
            bender.setHolding(0, left == 1);
        }
        if (mid != -1) {
            if (bender.currAbility != null && bender.castTime == null) {
                bender.currAbility.onMiddleClick(bender, mid == 1);
            }
            bender.setHolding(1, mid == 1);
        }
        if (right != -1) {
            if (bender.currAbility != null && bender.castTime == null) {
                bender.currAbility.onRightClick(bender, right == 1);
            }
            bender.setHolding(2, right == 1);
        }
    }
}
