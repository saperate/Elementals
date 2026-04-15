package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.network.payload.C2S.BuyUpgradePayload;
import dev.saperate.elementals.network.payload.C2S.ToggleUpgradePayload;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ToggleUpgradeC2SPacket {
    public static void receive(ServerPlayerEntity player, ToggleUpgradePayload payload) {
        // Everything here happens ONLY on the Server!
        String name = payload.name();
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
    }

}
