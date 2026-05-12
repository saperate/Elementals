package dev.saperate.elementals.platform;

import dev.saperate.elementals.platform.services.IEventsHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.function.Consumer;

public class NeoForgeEventsHelper implements IEventsHelper {
    @Override
    public void onPlayerJoin(Consumer<ServerPlayer> method) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                method.accept(player);
            }
        });
    }

    @Override
    public void onPlayerDisconnect(Consumer<ServerPlayer> method) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                method.accept(player);
            }
        });
    }

    @Override
    public void onPlayerRespawn(Consumer<ServerPlayer> method) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                method.accept(player);
            }
        });
    }

    @Override
    public void onServerClose(Consumer<MinecraftServer> method) {
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> {
            method.accept(event.getServer());
        });
    }

    @Override
    public void onServerTick(Consumer<MinecraftServer> method) {
        NeoForge.EVENT_BUS.addListener((ServerTickEvent event) -> {
            method.accept(event.getServer());
        });
    }

    @Override
    public void onClientJoin(Consumer<Minecraft> method) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity().level().isClientSide) {
                method.accept(Minecraft.getInstance());
            }
        });
    }

    @Override
    public void onClientTick(Consumer<Minecraft> method) {
        NeoForge.EVENT_BUS.addListener((ClientTickEvent event) -> {
            method.accept(Minecraft.getInstance());
        });
    }

    @Override
    public void onClientRenderOverlay(LayeredDraw.Layer layer) {
        NeoForge.EVENT_BUS.addListener((CustomizeGuiOverlayEvent event) -> {
            //Might cause some issues if we want animations, figure out how to get a DeltaTracker here
            layer.render(event.getGuiGraphics(), DeltaTracker.ZERO);
        });
    }

}
