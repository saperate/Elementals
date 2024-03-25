package dev.saperate.elementals.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Element{
    public static final List<Element> elementList = new ArrayList<>();
    public final List<Ability> abilityList = new ArrayList<>();
    public final String name;

    public Element(String name){
        this.name = name;
        elementList.add(this);
    }

    public void addAbility(Ability a){
        if(!abilityList.contains(a)){
            abilityList.add(a);
        }
    }

    public Ability getAbility(int index){
        return abilityList.get(index);
    }

    public void removeAbility(Ability a){
        abilityList.remove(a);
    }

    public boolean contains(Ability ability){
        return abilityList.contains(ability);
    }

}
