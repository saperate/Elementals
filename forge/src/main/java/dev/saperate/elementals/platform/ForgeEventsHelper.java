package dev.saperate.elementals.platform;

import dev.saperate.elementals.platform.services.IEventsHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
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

    @Override
    public void onClientJoin(Consumer<Minecraft> method) {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity().level().isClientSide) {
                method.accept(Minecraft.getInstance());
            }
        });
    }

    @Override
    public void onClientTick(Consumer<Minecraft> method) {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            method.accept(Minecraft.getInstance());
        });
    }

    @Override
    public void onClientRenderOverlay(LayeredDraw.Layer layer) {
        MinecraftForge.EVENT_BUS.addListener((CustomizeGuiOverlayEvent event) -> {
            //Might cause some issues if we want animations, figure out how to get a DeltaTracker here
            layer.render(event.getGuiGraphics(), DeltaTracker.ZERO);
        });
    }

}
