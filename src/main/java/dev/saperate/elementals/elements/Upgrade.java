package dev.saperate.elementals.elements;


import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * This serves as a node to make an upgrade tree, each node <b>MUST</b> have a different name, otherwise it can break stuff
 */
public final class Upgrade {
    public Upgrade parent;
    public Upgrade[] children = new Upgrade[0];
    public String name;
    public float localX;
    public int mod = 0;
    public boolean exclusive = false;
    public final int price; //in levels

    public Upgrade(String name, Upgrade[] children, boolean exclusive, int mod, int price) {
        this(name, children, exclusive, price);
        this.mod = mod;
    }

    public Upgrade(String name, Upgrade[] children, boolean exclusive, int price) {
        this(name, children, price);
        this.exclusive = exclusive;
    }

    public Upgrade(String name, Upgrade[] children, int price) {
        this(name,price);
        this.children = children;
        for (Upgrade child : children) {
            child.setParent(this);
        }
    }

    public Upgrade(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public void onSave(@NotNull NbtCompound nbtCompound, HashMap<Upgrade,Boolean> plrUpgrades) {
        NbtCompound nbt = new NbtCompound();
        if (plrUpgrades.containsKey(this)) {
            nbt.putBoolean("isActive", plrUpgrades.get(this));
            nbtCompound.put(name, nbt);
            for (Upgrade c : children) {
                c.onSave(nbtCompound, plrUpgrades);
            }
        }
    }

    public void onRead(@NotNull NbtCompound nbtCompound, HashMap<Upgrade,Boolean> plrUpgrades) {
        if (nbtCompound.contains(name)) {
            NbtCompound nbt = nbtCompound.getCompound(name);
            plrUpgrades.put(this, nbt.getBoolean("isActive"));
            for (Upgrade c : children) {
                c.onRead(nbtCompound, plrUpgrades);
            }
        }
    }


    public Upgrade[] nextUpgrades(HashMap<Upgrade,Boolean> plrUpgrades) {
        if (!plrUpgrades.containsKey(this)) {
            return new Upgrade[]{this};
        }
        ArrayList<Upgrade> upgrades = new ArrayList<>();
        for (Upgrade c : children) {
            Collections.addAll(upgrades, c.nextUpgrades(plrUpgrades));
        }
        return upgrades.toArray(Upgrade[]::new);
    }

    /**
     * Checks if this upgrade is part the next buy-able upgrades list.
     * This does <b>NOT</b> check if the player can afford it with price and whatnot.
     * @param plrUpgrades The list of upgrades that the player has
     * @return Whether the player can buy this upgrade or not.
     */
    public boolean canBuy(HashMap<Upgrade,Boolean> plrUpgrades) {
        if (parent == null || !parent.exclusive) {
            return true;
        }
        for (Upgrade child : parent.children) {
            if (plrUpgrades.containsKey(child)) {
                return false;
            }
        }
        return true;
    }

    public Upgrade getUpgradeByNameRecursive(String upgradeName) {
        if (name.equals(upgradeName)) {
            return this;
        }

        for (Upgrade child : children) {
            Upgrade u = child.getUpgradeByNameRecursive(upgradeName);
            if(u != null){
                return u;
            }
        }

        return null;
    }

    /**
     * This method calculates where on a tree graph our upgrade should be placed on the X axis.
     * It does not take into account conflicts between branches.
     * @see <a href="https://rachel53461.wordpress.com/2014/04/20/algorithm-for-drawing-trees/">Rachel Lim's Blog post</a>
     */
    public void calculateXPos(){
        float middle = (children.length-1)/2.0f;
        for (int i = 0; i < children.length; i++) {
            Upgrade child = children[i];
            child.localX = i - middle;
            child.calculateXPos();
        }
    }

    public Upgrade getHead(){
        if(parent.parent == null){//we don't want to get to the starting node, just the start
            return this;
        }
        return parent.getHead();
    }

    public float getPositionX(){
        return localX + mod;
    }

    public void setParent(Upgrade parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Upgrade other){
            return other.hashCode() == hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
