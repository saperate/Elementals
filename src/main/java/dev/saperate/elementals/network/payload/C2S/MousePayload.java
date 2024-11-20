package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record MousePayload(NbtCompound data) implements CustomPayload {
    public static final Id<MousePayload> ID = new Id<>(ModMessages.MOUSE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, MousePayload> CODEC = PacketCodec.tuple(PacketCodecs.NBT_COMPOUND, MousePayload::data, MousePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
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
}
