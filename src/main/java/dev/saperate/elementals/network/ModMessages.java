package dev.saperate.elementals.network;

import dev.saperate.elementals.network.packets.Ability1C2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ModMessages {

    public static final Identifier ABILITY1_PACKET_ID = new Identifier(MODID,"ability1");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(ABILITY1_PACKET_ID, Ability1C2SPacket::receive);
    }

    public static void registerS2CPackets(){

    }
}
