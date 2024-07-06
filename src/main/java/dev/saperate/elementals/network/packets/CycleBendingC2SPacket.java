package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.utils.MathHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class CycleBendingC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        // Everything here happens ONLY on the Server!
        server.execute(() -> {
            Bender bender = Bender.getBender(player);
            PlayerData data = bender.getData();

            if (data.activeElementIndex >= data.elements.size() - 1) {
                bender.setElement(0, true);
            } else {
                int newIndex = (int) MathHelper.clamp(data.activeElementIndex + 1, 0, data.elements.size() - 1);
                bender.setElement(newIndex, true);
            }
        });

    }

}
