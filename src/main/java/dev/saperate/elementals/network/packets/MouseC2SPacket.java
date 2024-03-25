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
        boolean left = buf.readBoolean();
        boolean mid = buf.readBoolean();
        boolean right = buf.readBoolean();


        Bender bender = Bender.getBender(player);
        if(bender.currAbility == null){
            return;
        }
        if(left){
            bender.currAbility.onLeftClick();
        }
        if(mid){
            bender.currAbility.onMiddleClick();
        }
        if(right){
            bender.currAbility.onRightClick();
        }
    }

}
