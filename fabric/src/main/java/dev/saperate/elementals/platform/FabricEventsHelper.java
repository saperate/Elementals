package dev.saperate.elementals.platform;

import dev.saperate.elementals.platform.services.IEventsHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class FabricEventsHelper implements IEventsHelper {
    @Override
    public void onPlayerJoin(Consumer<ServerPlayer> method) {
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) 
                        -> method.accept(handler.player));
    }

    @Override
    public void onPlayerDisconnect(Consumer<ServerPlayer> method) {
        ServerPlayConnectionEvents.DISCONNECT.register((
                (handler, server) 
                        -> method.accept(handler.player)));
    }

    @Override
    public void onPlayerRespawn(Consumer<ServerPlayer> method) {
        ServerPlayerEvents.AFTER_RESPAWN.register((
                (oldPlayer, newPlayer, alive) ->
                        method.accept(newPlayer)
        ));
    }

    @Override
    public void onServerClose(Consumer<MinecraftServer> method) {
        ServerLifecycleEvents.SERVER_STOPPING.register((method::accept));
    }

    @Override
    public void onServerTick(Consumer<MinecraftServer> method) {
        ServerTickEvents.END_SERVER_TICK.register(method::accept);
    }

    @Override
    public void onClientJoin(Consumer<Minecraft> method) {
        ClientPlayConnectionEvents.JOIN.register(
                (packetListener, packetSender, minecraft)->{
            method.accept(minecraft);
        });
    }
}
