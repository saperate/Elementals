package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.water.WaterElement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static dev.saperate.elementals.network.ModMessages.SYNC_ELEMENT_PACKET_ID;

public class Bender {
    public static Map<UUID, Bender> benders = new HashMap<>();
    public final PlayerEntity player;
    private Element element;
    public Ability[] boundAbilities = new Ability[3];
    public Long castTime;
    @Nullable
    public Ability currAbility;
    @Nullable
    public Object abilityData;

    public Bender(PlayerEntity player, Element element) {
        this.player = player;
        if (element == null) {
            this.element = Element.elementList.get(0);
        } else {
            this.element = element;
        }

        benders.put(player.getUuid(), this);
    }

    public void bindAbility(Ability ability, int index) {
        if (element.contains(ability) && index >= 0 && index <= 3) {
            boundAbilities[index] = ability;
            if (!player.getWorld().isClient) {
                StateDataSaverAndLoader.getServerState(player.getServer()).players.get(player.getUuid()).boundAbilities = boundAbilities;
            }
        }
    }


    public void clearBindings() {
        boundAbilities = new Ability[5];
        StateDataSaverAndLoader.getPlayerState(player).boundAbilities = new Ability[5];
    }

    public void bend(int index) {
        if (index >= 0 && index < 5 && boundAbilities[index] != null ) {
            if (castTime == null && currAbility == null) {
                castTime = System.currentTimeMillis();
                setCurrAbility(boundAbilities[index]);
                return;
            }
            if(currAbility != null && castTime != null){
                currAbility.onCall(this, System.currentTimeMillis() - castTime);
                castTime = null;
            }
        }
    }

    public static Bender getBender(PlayerEntity player) {
        return benders.get(player.getUuid());
    }

    public void setCurrAbility(Ability ability) {
        if(ability == null){
            castTime = null;
            abilityData = null;
        }
        this.currAbility = ability;
    }

    public void setCurrAbility(int i) {
        setCurrAbility(boundAbilities[i]);
    }

    public void setElement(Element element, boolean sync) {
        if (element == null) {
            this.element = Element.elementList.get(0);
        } else {
            this.element = element;
        }

        if (sync && !player.getWorld().isClient && player.getServer() != null) {
            StateDataSaverAndLoader.getServerState(player.getServer()).players.get(player.getUuid()).element = element;
            syncBending(this);
        }
    }

    public Element getElement() {
        return element;
    }

    public static void syncBending(Bender bender) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(bender.getElement().name);
        ServerPlayNetworking.send((ServerPlayerEntity) bender.player, SYNC_ELEMENT_PACKET_ID, buf);
    }
}
