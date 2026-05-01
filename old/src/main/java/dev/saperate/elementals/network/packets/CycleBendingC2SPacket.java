package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.network.payload.C2S.CycleBendingPayload;
import dev.saperate.elementals.utils.MathHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class CycleBendingC2SPacket {
    public static void receive(ServerPlayerEntity player, CycleBendingPayload payload) {
        // Everything here happens ONLY on the Server!
        Bender bender = Bender.getBender(player);
        PlayerData data = bender.getData();

        int nextIndex = data.activeElementIndex + (player.isSneaking() ? -1 : 1);
        if(nextIndex > data.elements.size() - 1){
            nextIndex = 0;
        } else if (nextIndex < 0) {
            nextIndex = data.elements.size() - 1;
        }

        bender.setElement(nextIndex, true);
    }

}
