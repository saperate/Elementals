package dev.saperate.elementals.packets;

import dev.saperate.elementals.ElementalsClient;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.network.payload.S2C.SyncChiPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;

public class SyncChiS2CPacket implements ElementalsClient.ElementalPacket<SyncChiPayload>{

    @Override
    public void receive(MinecraftClient client, SyncChiPayload payload) {
        if (client.player == null) {
            return;
        }
        ClientBender.get().chi = payload.chi();
    }

    @Override
    public CustomPayload.Id<SyncChiPayload> getId() {
        return SyncChiPayload.ID;
    }
}
