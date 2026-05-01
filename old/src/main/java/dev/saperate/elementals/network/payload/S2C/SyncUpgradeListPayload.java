package dev.saperate.elementals.network.payload.S2C;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncUpgradeListPayload(NbtCompound data) implements CustomPayload {
    public static final Id<SyncUpgradeListPayload> ID = new Id<>(ModMessages.SYNC_UPGRADE_LIST_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncUpgradeListPayload> CODEC = PacketCodec.tuple(PacketCodecs.NBT_COMPOUND, SyncUpgradeListPayload::data, SyncUpgradeListPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
