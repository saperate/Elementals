package dev.saperate.elementals.packets;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.ClientBender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.Optional;

import static dev.saperate.elementals.network.ModMessages.GET_MOD_VERSION;
import static dev.saperate.elementals.network.ModMessages.GET_UPGRADE_LIST_PACKET_ID;

public class SendModVersionS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        PacketByteBuf response = PacketByteBufs.create();

        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(Elementals.MODID);
        if(container.isPresent()){
            Version modVersion = container.get().getMetadata().getVersion();
            response.writeString(modVersion.toString());
        }else {
            response.writeString("No ModContainer found");
        }

        ClientPlayNetworking.send(GET_MOD_VERSION, response);
    }

}
