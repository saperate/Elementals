package dev.saperate.elementals.network;

import dev.saperate.elementals.network.packets.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ModMessages {
    public static final Identifier MOUSE_PACKET_ID = new Identifier(MODID,"mouse");
    public static final Identifier ABILITY1_PACKET_ID = new Identifier(MODID,"ability1");
    public static final Identifier ABILITY2_PACKET_ID = new Identifier(MODID,"ability2");
    public static final Identifier ABILITY3_PACKET_ID = new Identifier(MODID,"ability3");
    public static final Identifier ABILITY4_PACKET_ID = new Identifier(MODID,"ability4");
    public static final Identifier SYNC_CURR_ABILITY_PACKET_ID = new Identifier(MODID,"curr_ability");
    public static final Identifier SYNC_ELEMENT_PACKET_ID = new Identifier(MODID,"element");
    public static final Identifier SYNC_UPGRADE_LIST_PACKET_ID = new Identifier(MODID,"sync_upgrades");
    public static final Identifier GET_UPGRADE_LIST_PACKET_ID = new Identifier(MODID,"get_upgrades");
    public static final Identifier BUY_UPGRADE_PACKET_ID = new Identifier(MODID,"buy_upgrade");
    public static final Identifier SYNC_CHI_PACKET_ID = new Identifier(MODID,"sync_chi");
    public static final Identifier SYNC_LEVEL_PACKET_ID = new Identifier(MODID,"sync_level");
    public static final Identifier CYCLE_BENDING_PACKET_ID = new Identifier(MODID,"cycle_bending");
    public static final Identifier GET_MOD_VERSION_PACKET_ID = new Identifier(MODID,"get_mod_version");
    public static final Identifier SEND_MOD_VERSION_PACKET_ID = new Identifier(MODID,"send_mod_version");


    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(MOUSE_PACKET_ID, MouseC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ABILITY1_PACKET_ID, Ability1C2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ABILITY2_PACKET_ID, Ability2C2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ABILITY3_PACKET_ID, Ability3C2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ABILITY4_PACKET_ID, Ability4C2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_UPGRADE_LIST_PACKET_ID, GetUpgradeListC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(BUY_UPGRADE_PACKET_ID, BuyUpgradeC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SYNC_LEVEL_PACKET_ID, SyncLevelC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CYCLE_BENDING_PACKET_ID, CycleBendingC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(GET_MOD_VERSION_PACKET_ID, GetModVersionC2SPacket::receive);

    }

}
