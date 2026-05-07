package dev.saperate.elementals.network.packets.C2S;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.network.ElementalsNetworking;
import net.minecraft.SharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncVersionPacket(String version) {
    public static final StreamCodec<FriendlyByteBuf, SyncVersionPacket> STREAM_CODEC = StreamCodec.ofMember(SyncVersionPacket::encode, SyncVersionPacket::new);


    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ElementalsNetworking.SYNC_MOD_VERSION_PACKET_ID);
    }

    public SyncVersionPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(version);
    }

    public static void handle(PacketContext<SyncVersionPacket> ctx) {
        ElementalsNetworking.expectSideOrThrow(ctx.side(), Side.SERVER);
        String version = ctx.message().version;
        String serverVersion = getModVersion();


        if (serverVersion.isEmpty()) {
            ctx.sender().connection.disconnect(Component.literal("Unable to get server mod version"));
            return;
        }

        if (!version.equals(serverVersion)) {
            ctx.sender().connection.disconnect(Component.literal("Version mismatch! Client (" + version + ") != Server (" + serverVersion + ")"));
        }
    }

    public static String getModVersion() {
        return SharedConstants.getCurrentVersion().getName();
    }
}
