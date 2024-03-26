package dev.saperate.elementals.network;

import dev.saperate.elementals.network.packets.Ability1C2SPacket;
import dev.saperate.elementals.network.packets.Ability2C2SPacket;
import dev.saperate.elementals.network.packets.MouseC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ModMessages {
    public static final Identifier MOUSE_PACKET_ID = new Identifier(MODID,"mouse");
    public static final Identifier ABILITY1_PACKET_ID = new Identifier(MODID,"ability1");
    public static final Identifier ABILITY2_PACKET_ID = new Identifier(MODID,"ability2");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(MOUSE_PACKET_ID, MouseC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ABILITY1_PACKET_ID, Ability1C2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ABILITY2_PACKET_ID, Ability2C2SPacket::receive);
    }

    public static void registerS2CPackets(){

    }
}
