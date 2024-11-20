package dev.saperate.elementals.network.payload.S2C;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncLevelPayload(NbtCompound data) implements CustomPayload {
    public static final Id<SyncLevelPayload> ID = new Id<>(ModMessages.SYNC_LEVEL_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncLevelPayload> CODEC = PacketCodec.tuple(PacketCodecs.NBT_COMPOUND, SyncLevelPayload::data, SyncLevelPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public int getLevel(){
        return data.getInt("level");
    }

    public float getXP(){
        return data.getFloat("xp");
    }
}
