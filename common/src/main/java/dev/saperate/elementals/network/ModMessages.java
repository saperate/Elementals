package dev.saperate.elementals.network;

import commonnetwork.api.Network;
import commonnetwork.networking.data.Side;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.network.packets.C2S.AbilityPacket;
import dev.saperate.elementals.network.packets.C2S.BuyUpgradePacket;
import dev.saperate.elementals.network.packets.S2C.SyncChiPacket;
import dev.saperate.elementals.network.payload.C2S.*;
import dev.saperate.elementals.network.payload.S2C.*;
import net.minecraft.resources.ResourceLocation;

public class ModMessages {
    //S2C
    public static final ResourceLocation SYNC_CHI_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "sync_chi");
    public static final ResourceLocation SYNC_CURR_ABILITY_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "curr_ability");
    public static final ResourceLocation SYNC_ELEMENT_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "element");
    public static final ResourceLocation SYNC_LEVEL_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "sync_level");
    public static final ResourceLocation SYNC_UPGRADE_LIST_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "sync_upgrades");

    //C2S
    public static final ResourceLocation MOUSE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "mouse");
    public static final ResourceLocation ABILITY_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "ability");
    public static final ResourceLocation GET_UPGRADE_LIST_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "get_upgrades");
    public static final ResourceLocation BUY_UPGRADE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "buy_upgrade");
    public static final ResourceLocation TOGGLE_UPGRADE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "toggle_upgrade");
    public static final ResourceLocation CYCLE_BENDING_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "cycle_bending");
    public static final ResourceLocation REQUEST_SYNC_LEVEL_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "request_sync_level");
    public static final ResourceLocation GET_MOD_VERSION_PACKET_ID = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "get_mod_version");


    public static void registerNetworking() {
        //S2C payloads
        Network.registerPacket(SyncChiPacket.type(), SyncChiPacket.class, SyncChiPacket.STREAM_CODEC, SyncChiPacket::handle);
        PayloadTypeRegistry.playS2C().register(SyncChiPayload.ID, SyncChiPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncCurrAbilityPayload.ID, SyncCurrAbilityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncElementsPayload.ID, SyncElementsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncLevelPayload.ID, SyncLevelPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncUpgradeListPayload.ID, SyncUpgradeListPayload.CODEC);

        //C2S payloads
        Network.registerPacket(AbilityPacket.type(), AbilityPacket.class, AbilityPacket.STREAM_CODEC, AbilityPacket::handle);
        Network.registerPacket(BuyUpgradePacket.type(), BuyUpgradePacket.class, BuyUpgradePacket.STREAM_CODEC, BuyUpgradePacket::handle);
        PayloadTypeRegistry.playC2S().register(RequestSyncLevelPayload.ID, RequestSyncLevelPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSyncUpgradeListPayload.ID, RequestSyncUpgradeListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MousePayload.ID, MousePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CycleBendingPayload.ID, CycleBendingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BuyUpgradePayload.ID, BuyUpgradePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleUpgradePayload.ID, ToggleUpgradePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SyncVersionPayload.ID, SyncVersionPayload.CODEC);
    }
    
    public static void isOnSideOrThrow(Side currentSide, Side expectedSide){
        if(!currentSide.equals(expectedSide)){
            throw new RuntimeException("current side was not the same as the expected side!");
        }
    }
}
