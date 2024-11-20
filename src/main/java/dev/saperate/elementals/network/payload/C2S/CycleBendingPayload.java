package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CycleBendingPayload(boolean data) implements CustomPayload {
    //TODO make data be true when sneaking and cycle in reverse
    public static final Id<CycleBendingPayload> ID = new Id<>(ModMessages.CYCLE_BENDING_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, CycleBendingPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, CycleBendingPayload::data, CycleBendingPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
