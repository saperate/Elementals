package dev.saperate.elementals.keys;


import dev.saperate.elementals.network.packets.MouseC2SPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import static dev.saperate.elementals.network.ModMessages.MOUSE_PACKET_ID;

public abstract class MouseInput {

    public static void registerMouseClickEvent(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null) {
                boolean left = client.mouse.wasLeftButtonClicked();
                boolean mid = client.mouse.wasMiddleButtonClicked();
                boolean right = client.mouse.wasRightButtonClicked();
                if(mid || right || left){
                    PacketByteBuf packet = PacketByteBufs.create();
                    packet.writeBoolean(left);
                    packet.writeBoolean(mid);
                    packet.writeBoolean(right);

                    ClientPlayNetworking.send(MOUSE_PACKET_ID, packet);
                }
            }
        });
    }
}
