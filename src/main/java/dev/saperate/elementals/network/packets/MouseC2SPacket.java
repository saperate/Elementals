package dev.saperate.elementals.network.packets;

import dev.saperate.elementals.data.Bender;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class MouseC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        // Everything here happens ONLY on the Server!
        int left = buf.readInt();
        int mid = buf.readInt();
        int right = buf.readInt();


        Bender bender = Bender.getBender(player);
        if(bender.currAbility == null || bender.castTime != null){
            return;
        }
        if(left != -1){
            server.execute(() -> {
                bender.currAbility.onLeftClick(bender, left == 1);
            });
        }
        if(mid != -1){
            server.execute(() -> {
                bender.currAbility.onMiddleClick(bender, mid == 1);
            });
        }
        if(right != -1){
            server.execute(() -> {
                bender.currAbility.onRightClick(bender, right == 1);
            });
        }
    }

}
