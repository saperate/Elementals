package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ToggleUpgradeC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        // Everything here happens ONLY on the Server!
        String name = buf.readString();
        server.execute(() -> {
            Bender bender = Bender.getBender(player);

            if (name.startsWith("bending")) {
                return;
            }

            Upgrade upgrade = bender.getElement().root.getUpgradeByNameRecursive(name);
            if (upgrade == null) {
                return;
            }

            PlayerData plrData = PlayerData.get(player);
            plrData.toggleUpgrade(upgrade);
            GetUpgradeListC2SPacket.send(player);
            SyncLevelC2SPacket.send(player);

        });
    }

}
