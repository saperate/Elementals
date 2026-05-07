package dev.saperate.elementals.network;

import commonnetwork.api.Network;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.network.packets.C2S.*;
import dev.saperate.elementals.network.packets.S2C.SyncChiPacket;
import dev.saperate.elementals.network.packets.S2C.SyncCurrAbilityPacket;
import dev.saperate.elementals.network.packets.S2C.SyncElementsPacket;
import dev.saperate.elementals.network.packets.common.SyncLevelPacket;
import dev.saperate.elementals.network.packets.common.SyncUpgradeListPacket;
import net.minecraft.resources.ResourceLocation;

public class ElementalsNetworking {
    //S2C
    public static final ResourceLocation SYNC_CHI_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "sync_chi");
    public static final ResourceLocation SYNC_CURR_ABILITY_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "curr_ability");
    public static final ResourceLocation SYNC_ELEMENT_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "element");

    //C2S
    public static final ResourceLocation MOUSE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "mouse");
    public static final ResourceLocation ABILITY_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "ability");
    public static final ResourceLocation BUY_UPGRADE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "buy_upgrade");
    public static final ResourceLocation TOGGLE_UPGRADE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "toggle_upgrade");
    public static final ResourceLocation CYCLE_BENDING_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "cycle_bending");
    public static final ResourceLocation SYNC_MOD_VERSION_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "get_mod_version");

    //COMMON
    public static final ResourceLocation SYNC_LEVEL_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "sync_level");
    public static final ResourceLocation SYNC_UPGRADE_LIST_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "sync_upgrades");

    public static void register() {
        //S2C payloads
        Network.registerPacket(SyncChiPacket.type(), SyncChiPacket.class, SyncChiPacket.STREAM_CODEC, SyncChiPacket::handle);
        Network.registerPacket(SyncElementsPacket.type(), SyncElementsPacket.class, SyncElementsPacket.STREAM_CODEC, SyncElementsPacket::handle);
        Network.registerPacket(SyncCurrAbilityPacket.type(), SyncCurrAbilityPacket.class, SyncCurrAbilityPacket.STREAM_CODEC, SyncCurrAbilityPacket::handle);

        //C2S payloads
        Network.registerPacket(AbilityPacket.type(), AbilityPacket.class, AbilityPacket.STREAM_CODEC, AbilityPacket::handle);
        Network.registerPacket(BuyUpgradePacket.type(), BuyUpgradePacket.class, BuyUpgradePacket.STREAM_CODEC, BuyUpgradePacket::handle);
        Network.registerPacket(CycleBendingPacket.type(), CycleBendingPacket.class, CycleBendingPacket.STREAM_CODEC, CycleBendingPacket::handle);
        Network.registerPacket(MouseClickPacket.type(), MouseClickPacket.class, MouseClickPacket.STREAM_CODEC, MouseClickPacket::handle);
        Network.registerPacket(ToggleUpgradePacket.type(), ToggleUpgradePacket.class, ToggleUpgradePacket.STREAM_CODEC, ToggleUpgradePacket::handle);
        Network.registerPacket(SyncVersionPacket.type(), SyncVersionPacket.class, SyncVersionPacket.STREAM_CODEC, SyncVersionPacket::handle);
        Network.registerPacket(SyncLevelPacket.type(), SyncLevelPacket.class, SyncLevelPacket.STREAM_CODEC, SyncLevelPacket::handle);
        Network.registerPacket(SyncUpgradeListPacket.type(), SyncUpgradeListPacket.class, SyncUpgradeListPacket.STREAM_CODEC, SyncUpgradeListPacket::handle);
    }
    
    public static void expectSideOrThrow(Side currentSide, Side expectedSide){
        if(!currentSide.equals(expectedSide)){
            throw new RuntimeException("current side was not the same as the expected side!");
        }
    }
}
