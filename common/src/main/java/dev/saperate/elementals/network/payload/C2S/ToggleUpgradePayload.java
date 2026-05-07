package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ToggleUpgradePayload(String name) implements CustomPayload {
    public static final Id<ToggleUpgradePayload> ID = new Id<>(ModMessages.TOGGLE_UPGRADE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ToggleUpgradePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, ToggleUpgradePayload::name, ToggleUpgradePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
