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
        if(index == -1){
            return null;
        }
        return abilityList.get(index);
    }

    public void removeAbility(Ability a){
        abilityList.remove(a);
    }

    public boolean contains(Ability ability){
        return abilityList.contains(ability);
    }

    public static Element getElementByName(String name){
        for(Element e : elementList){
            if(e.name.equalsIgnoreCase(name)){
                return e;
            }
        }
        return elementList.get(0);//none
    }

    public String getName() {
        return name;
    }
}
