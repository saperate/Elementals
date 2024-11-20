package dev.saperate.elementals.packets;

import dev.saperate.elementals.ElementalsClient;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.network.payload.S2C.SyncCurrAbilityPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;

public class SyncCurrAbilityS2CPacket implements ElementalsClient.ElementalPacket<SyncCurrAbilityPayload> {
    @Override
    public void receive(MinecraftClient client, SyncCurrAbilityPayload payload) {
        if(client.player == null){
            return;
        }
        int i = payload.abilityIndex();
        ClientBender bender = ClientBender.get();
        if(bender.getElement() == null){
            return;
        }
        if(i == -1){
            bender.currAbility = null;
            return;
        }
        bender.currAbility = bender.getElement().getAbility(i);
    }

    @Override
    public CustomPayload.Id<SyncCurrAbilityPayload> getId() {
        return SyncCurrAbilityPayload.ID;
    }
}
