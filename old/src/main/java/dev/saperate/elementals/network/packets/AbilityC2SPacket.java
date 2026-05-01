package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.network.payload.C2S.AbilityPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public class AbilityC2SPacket {
    public static void receive(ServerPlayerEntity player, AbilityPayload payload) {
        // Everything here happens ONLY on the Server!
        Bender.getBender(player).bend(payload.getIndex(), payload.isStart());
    }

}
