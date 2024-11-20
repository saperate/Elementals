package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RequestSyncUpgradeListPayload(boolean unused) implements CustomPayload {
    public static final Id<RequestSyncUpgradeListPayload> ID = new Id<>(ModMessages.SYNC_UPGRADE_LIST_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, RequestSyncUpgradeListPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, RequestSyncUpgradeListPayload::unused, RequestSyncUpgradeListPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
