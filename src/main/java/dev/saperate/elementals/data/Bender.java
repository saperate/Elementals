package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.water.WaterElement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static dev.saperate.elementals.network.ModMessages.SYNC_ELEMENT_PACKET_ID;

public class Bender {
    public static final Map<UUID,Bender> benders = new HashMap<>();
    public final PlayerEntity player;
    private Element element;
    public Ability[] boundAbilities = new Ability[3];
    @Nullable
    public Ability currAbility;

    public Bender(PlayerEntity player, Element element) {
        this.player = player;
        if(element == null){
            this.element = Element.elementList.get(0);
        }else{
            this.element = element;
        }

        benders.put(player.getUuid(), this);
    }

    public void bindAbility(Ability ability, int index){
        if(element.contains(ability) && index >= 0 && index < 3){
            boundAbilities[index] = ability;
            if(!player.getWorld().isClient){
                StateDataSaverAndLoader.getServerState(player.getServer()).players.get(player.getUuid()).boundAbilities = boundAbilities;
            }
        }
    }

    public void bend(int index){
        if(index >= 0 && index < 5 && boundAbilities[index] != null && currAbility == null){
            boundAbilities[index].onCall(this);
        }
    }

    public static Bender getBender(PlayerEntity player){
        return benders.get(player.getUuid());
    }

    public void setCurrAbility(Ability ability){
        this.currAbility = ability;
    }

    public void setCurrAbility(int i){
        setCurrAbility(boundAbilities[i]);
    }

    public void setElement(Element element) {
        if(element == null){
            this.element = Element.elementList.get(0);
        }else{
            this.element = element;
        }

        if(!player.getWorld().isClient){
            StateDataSaverAndLoader.getServerState(player.getServer()).players.get(player.getUuid()).element = element;
            //TODO remove this
            int abilitySize = this.element.abilityList.size();
            if(abilitySize >= 1){
                bindAbility(this.element.getAbility(0),0);
            }
            if(abilitySize >= 2){
                bindAbility(this.element.getAbility(1),1);
            }
            syncBending(this);
        }
    }

    public Element getElement() {
        return element;
    }

    public static void syncBending(Bender bender){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(bender.getElement().name);
        ServerPlayNetworking.send((ServerPlayerEntity) bender.player,SYNC_ELEMENT_PACKET_ID,buf);
    }
}
