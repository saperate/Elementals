package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.water.WaterElement;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Bender {
    public static final Map<UUID,Bender> benders = new HashMap<>();
    public final PlayerEntity player;
    public Element element;
    public Ability[] boundAbility = new Ability[5];
    @Nullable
    public Ability currAbility;

    public Bender(PlayerEntity player, Element element) {
        this.player = player;
        this.element = element;
        benders.put(player.getUuid(), this);
        bindAbility(Element.elementList.get(0).getAbility(0),0);
    }

    public void bindAbility(Ability ability, int index){
        if(element.contains(ability) && index >= 0 && index < 5){
            boundAbility[index] = ability;
        }
    }

    public void bend(int index){
        if(index >= 0 && index < 5 && boundAbility[index] != null && currAbility == null){
            boundAbility[index].onCall(player);
        }
    }

    public static Bender getBender(PlayerEntity player){
        return benders.get(player.getUuid());
    }

    public void setCurrAbility(Ability ability){
        this.currAbility = ability;
    }
}
