package dev.saperate.elementals.network.payload.C2S;

import dev.saperate.elementals.network.ModMessages;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BuyUpgradePayload(String name) implements CustomPayload {
    public static final Id<BuyUpgradePayload> ID = new Id<>(ModMessages.BUY_UPGRADE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, BuyUpgradePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, BuyUpgradePayload::name, BuyUpgradePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
