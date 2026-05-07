package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import static dev.saperate.elementals.network.ModMessages.ABILITY_PACKET_ID;

public record AbilityPayload(NbtCompound data) implements CustomPayload {
    public static final Id<AbilityPayload> ID = new Id<>(ABILITY_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, AbilityPayload> CODEC = PacketCodec.tuple(PacketCodecs.NBT_COMPOUND, AbilityPayload::data, AbilityPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public int getIndex(){
        return data.getInt("index");
    }

    public boolean isStart(){
        return data.getBoolean("isStart");
    }
}
