package dev.saperate.elementals.platform;

import dev.saperate.elementals.platform.services.IEventsHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

import java.util.function.Consumer;

public class ForgeEventsHelper implements IEventsHelper {
    @Override
    public void onPlayerJoin(Consumer<ServerPlayer> method) {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                method.accept(player);
            }
        });
    }

    @Override
    public void onPlayerDisconnect(Consumer<ServerPlayer> method) {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                method.accept(player);
            }
        });
    }

    @Override
    public void onPlayerRespawn(Consumer<ServerPlayer> method) {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                method.accept(player);
            }
        });
    }

    @Override
    public void onServerClose(Consumer<MinecraftServer> method) {
        MinecraftForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> {
            method.accept(event.getServer());
        });
    }

    @Override
    public void onServerTick(Consumer<MinecraftServer> method) {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            method.accept(event.getServer());
        });
    }

}
