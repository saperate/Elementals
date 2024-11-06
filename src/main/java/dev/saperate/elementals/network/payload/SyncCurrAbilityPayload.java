package dev.saperate.elementals.network.payload;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncCurrAbilityPayload(int abilityIndex) implements CustomPayload {
    public static final Id<SyncCurrAbilityPayload> ID = new Id<>(ModMessages.SYNC_CURR_ABILITY_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SyncCurrAbilityPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SyncCurrAbilityPayload::abilityIndex, SyncCurrAbilityPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
