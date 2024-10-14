package dev.saperate.elementals.packets;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.gui.UpgradeTreeScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class UpdatePlayerStepHeightS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        if(client.player == null){
            return;
        }

        client.player.setStepHeight(buf.readFloat());
        System.out.println(client.player.getStepHeight());
    }
}
