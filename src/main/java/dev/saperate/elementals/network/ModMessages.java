package dev.saperate.elementals.network;

import dev.saperate.elementals.network.packets.*;
import dev.saperate.elementals.network.payload.C2S.*;
import dev.saperate.elementals.network.payload.S2C.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ModMessages {
    public static final Identifier MOUSE_PACKET_ID = Identifier.of(MODID, "mouse");
    public static final Identifier ABILITY_PACKET_ID = Identifier.of(MODID, "ability");
    public static final Identifier SYNC_CURR_ABILITY_PACKET_ID = Identifier.of(MODID, "curr_ability");
    public static final Identifier SYNC_ELEMENT_PACKET_ID = Identifier.of(MODID, "element");
    public static final Identifier SYNC_UPGRADE_LIST_PACKET_ID = Identifier.of(MODID, "sync_upgrades");
    public static final Identifier GET_UPGRADE_LIST_PACKET_ID = Identifier.of(MODID, "get_upgrades");
    public static final Identifier BUY_UPGRADE_PACKET_ID = Identifier.of(MODID, "buy_upgrade");
    public static final Identifier SYNC_CHI_PACKET_ID = Identifier.of(MODID, "sync_chi");
    public static final Identifier SYNC_LEVEL_PACKET_ID = Identifier.of(MODID, "sync_level");
    public static final Identifier CYCLE_BENDING_PACKET_ID = Identifier.of(MODID, "cycle_bending");
    public static final Identifier GET_MOD_VERSION_PACKET_ID = Identifier.of(MODID, "get_mod_version");
    public static final Identifier UPDATE_PLAYER_STEP_HEIGHT = Identifier.of(MODID, "update_player_step_height");


    public static void registerNetworking() {

        //S2C payloads
        PayloadTypeRegistry.playS2C().register(SyncChiPayload.ID, SyncChiPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncCurrAbilityPayload.ID, SyncCurrAbilityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncElementsPayload.ID, SyncElementsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncLevelPayload.ID, SyncLevelPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncUpgradeListPayload.ID, SyncUpgradeListPayload.CODEC);

        //C2S payloads
        PayloadTypeRegistry.playC2S().register(RequestSyncLevelPayload.ID, RequestSyncLevelPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSyncUpgradeListPayload.ID, RequestSyncUpgradeListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AbilityPayload.ID, AbilityPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MousePayload.ID, MousePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CycleBendingPayload.ID, CycleBendingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BuyUpgradePayload.ID, BuyUpgradePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SyncVersionPayload.ID, SyncVersionPayload.CODEC);

        //Receivers
        ServerPlayNetworking.registerGlobalReceiver(RequestSyncLevelPayload.ID, (payload, context) -> {
            context.server().execute(() -> SyncLevelC2SPacket.receive(context.player(), payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(RequestSyncUpgradeListPayload.ID, (payload, context) -> {
            context.server().execute(() -> GetUpgradeListC2SPacket.receive(context.player(), payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(AbilityPayload.ID, (payload, context) -> {
            context.server().execute(() -> AbilityC2SPacket.receive(context.player(), payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(CycleBendingPayload.ID, (payload, context) -> {
            context.server().execute(() -> CycleBendingC2SPacket.receive(context.player(), payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(MousePayload.ID, (payload, context) -> {
            context.server().execute(() -> MouseC2SPacket.receive(context.player(), payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(BuyUpgradePayload.ID, (payload, context) -> {
            context.server().execute(() -> BuyUpgradeC2SPacket.receive(context.player(), payload));
        });
        ServerPlayNetworking.registerGlobalReceiver(SyncVersionPayload.ID, (payload, context) -> {
            context.server().execute(() -> GetModVersionC2SPacket.receive(context.player(), payload));
        });

    }

}
