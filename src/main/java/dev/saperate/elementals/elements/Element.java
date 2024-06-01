package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

import static dev.saperate.elementals.utils.SapsUtils.extractBits;

public abstract class Element{
    //TODO consider changing this for a set
    public static final List<Element> elementList = new ArrayList<>();
    public final List<Ability> abilityList = new ArrayList<>();
    public final List<Ability> bindableAbilities = new ArrayList<>();
    public final String name;
    public Upgrade root;


    public Element(String name, Upgrade[] upgrades){
        if (upgrades.length > 4){
            throw new RuntimeException("Element " + name + " had more than 4 abilities in its upgrade path!");
        }
        this.name = name;
        elementList.add(this);
        this.root = new Upgrade(name,upgrades);
    }

    public Element(String name){
        this(name,new Upgrade[0]);
    }

    public void addAbility(Ability a, boolean bindable){
        if(!abilityList.contains(a)){
            abilityList.add(a);
            if(bindable){
                bindableAbilities.add(a);
            }
        }
    }

    public void addAbility(Ability a){
        addAbility(a,false);
    }

    public Ability getBindableAbility(int index){
        if(index == -1 || index >= bindableAbilities.size()){
            return null;
        }
        return bindableAbilities.get(index);
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

    public static Element getElementByName(String name){
        Element e  = getElementByNameNull(name);
        if(e == null){
            e = elementList.get(0);
        }
        return e;
    }

    public static Element getElementByNameNull(String name){
        for(Element e : elementList){
            if(e.name.equalsIgnoreCase(name)){
                return e;
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public NbtCompound onSave(HashMap<Upgrade,Boolean> plrUpgrades){
        NbtCompound nbt = new NbtCompound();
        for(Upgrade child : root.children){
            child.onSave(nbt,plrUpgrades);
        }
        return nbt;
    }

    public void onRead(NbtCompound nbt, HashMap<Upgrade,Boolean> plrUpgrades){
        for(Upgrade child : root.children){
            child.onRead(nbt,plrUpgrades);
        }
    }
}
