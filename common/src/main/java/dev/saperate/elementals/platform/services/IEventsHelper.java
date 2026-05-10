package dev.saperate.elementals.platform.services;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public interface IEventsHelper {
    void onPlayerJoin(Consumer<ServerPlayer> method);
    void onPlayerDisconnect(Consumer<ServerPlayer> method);
    void onPlayerRespawn(Consumer<ServerPlayer> method);
    void onServerClose(Consumer<MinecraftServer> method);
    void onServerTick(Consumer<MinecraftServer> method);
    void onClientJoin(Consumer<Minecraft> method);
    void onClientTick(Consumer<Minecraft> method);
}
