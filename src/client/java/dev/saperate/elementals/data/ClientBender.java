package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientBender {
    private static ClientBender instance;
    private ArrayList<Element> elements = new ArrayList<>();
    private int activeElementIndex = 0;
    public Ability currAbility;
    public PlayerEntity player;
    public HashMap<Upgrade, Boolean> upgrades = new HashMap<>();
    private double castTime = -1;
    public float chi = 100;
    public int level = 0;
    public float xp = 0;

    public void startCasting() {
        castTime = 0;
    }

    public void stopCasting(){
        castTime = -1;
    }

    public boolean isCasting(){
        return castTime != -1;
    }

    public double getCastTime() {
        return castTime;
    }

    public void tick(){

        if(castTime >= 0){
            castTime += 0.05;
        }
        //We don't cap chi to 100 here since we also want to know when we last used a move
        //so when it gets higher than 100 we can use that as a countdown for when we will not display the chi counter anymore
        chi += Bender.CHI_REGENERATION_RATE;
    }


    private ClientBender() {

    }

    public static ClientBender get() {
        if (instance == null) {
            instance = new ClientBender();
        }
        return instance;
    }

    public Element getElement(){
        return elements.get(activeElementIndex);
    }

    public void setActiveElementIndex(int index){
        activeElementIndex = index;
    }

    public void setElements(ArrayList<Element> elements){
        this.elements = elements;
    }

}
