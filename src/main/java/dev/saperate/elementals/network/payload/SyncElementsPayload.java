package dev.saperate.elementals.network.payload;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncElementsPayload(NbtCompound data) implements CustomPayload {
    public static final Id<SyncElementsPayload> ID = new Id<>(ModMessages.SYNC_CURR_ABILITY_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncElementsPayload> CODEC = PacketCodec.tuple(PacketCodecs.NBT_COMPOUND, SyncElementsPayload::data, SyncElementsPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public String getPackedElements(){
        return data.getString("packedElements");
    }

    public int getElementIndex(){
        return data.getInt("activeElement");
    }
}
