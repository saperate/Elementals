package dev.saperate.elementals.packets;

import dev.saperate.elementals.ElementalsClient;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.gui.UpgradeTreeScreen;
import dev.saperate.elementals.network.payload.SyncCurrAbilityPayload;
import dev.saperate.elementals.network.payload.SyncElementsPayload;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

public class SyncBendingElementS2CPacket implements ElementalsClient.ElementalPacket<SyncElementsPayload>{
    @Override
    public void receive(MinecraftClient client, SyncElementsPayload payload) {
        ClientBender bender = ClientBender.get();
        String elements = payload.getPackedElements();
        int activeElementIndex = payload.getElementIndex();

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

    @Override
    public CustomPayload.Id<SyncElementsPayload> getId() {
        return SyncElementsPayload.ID;
    }
}
