package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

import static dev.saperate.elementals.network.ModMessages.*;

public class GetModVersionC2SPacket {


    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        // Everything here happens ONLY on the Server!
        String version = buf.readString();
        Version serverVersion = getModVersion();


        if (serverVersion == null) {
            handler.disconnect(Text.of("Unable to get server mod version"));
            return;
        }

        if (!version.equals(serverVersion.getFriendlyString())) {
            if (version.equals("No ModContainer found")) {
                handler.disconnect(Text.of("Unable to get client mod version"));
                return;
            }
            handler.disconnect(Text.of("Version mismatch! Client (" + version + ") != Server (" + serverVersion.getFriendlyString() + ")"));
        }
    }


    public static Version getModVersion() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(Elementals.MODID);
        return container.map(modContainer -> modContainer.getMetadata().getVersion()).orElse(null);
    }
}
