package dev.saperate.elementals.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.Element;
import gui.UpgradeTreeScreen;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_ELEMENT_PACKET_ID;

public class SyncBendingElementS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        ClientBender bender = ClientBender.get();
        String e = buf.readString();

        client.execute(() -> {
            if (bender.element != null && e.equals(bender.element.name)) {
                return;
            }
            bender.element = Element.getElementByName(e);
            if (client.currentScreen instanceof UpgradeTreeScreen treeScreen) {
                treeScreen.close();
            }
        });
    }
}
