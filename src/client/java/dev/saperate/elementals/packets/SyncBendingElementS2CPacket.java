package dev.saperate.elementals.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.gui.UpgradeTreeScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class SyncBendingElementS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        ClientBender bender = ClientBender.get();
        String elements = buf.readString();
        int activeElementIndex = buf.readInt();

        client.execute(() -> {
            bender.setElements(Bender.unpackElementsFromString(elements));
            bender.setActiveElementIndex(activeElementIndex);
            if (client.currentScreen instanceof UpgradeTreeScreen treeScreen) {
                treeScreen.close();
            }
            if(bender.chi > 100){
                bender.chi = 100;
            }
        });
    }
}
