package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

import static dev.saperate.elementals.utils.SapsUtils.extractBits;

public abstract class Element{
    private static final HashMap<String,Element> elements = new HashMap<>();
    private final List<Ability> abilityList = new ArrayList<>();
    public final List<Ability> bindableAbilities = new ArrayList<>();
    public final String name;
    public Upgrade root;

    public Element(String name, Upgrade root){
        if(elements.containsKey(name.toLowerCase(Locale.CANADA))){
            throw new RuntimeException("Element \""+name+"\" was already registered!");
        }
        elements.put(name.toLowerCase(Locale.CANADA),this);
        this.name = name;
        this.root = root;
    }

    public Element(String name, Upgrade[] upgrades){
        this(name,new Upgrade(name,upgrades,0));
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

    public int getIndexOfAbility(Ability obj){
        return abilityList.indexOf(obj);
    }

    public boolean contains(Ability ability){
        return abilityList.contains(ability);
    }

    /**
     * Gets an element using its name. If it is not found, returns a reference to {@link NoneElement}
     */
    public static Element getElement(String name){
        return elements.getOrDefault(name.toLowerCase(Locale.CANADA),elements.get("None"));
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

    public abstract boolean isSkillTreeComplete(Bender bender);

    public int getColor(){
        return 0xFFa0e8e6;
    }

    public int getAccentColor(){
        return 0xFF13AEA9;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<Element> getElementList(){
        return elements.values().stream().toList();
    }
}
