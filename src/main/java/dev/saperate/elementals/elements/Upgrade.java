package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.PlayerData;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public final class Upgrade {
    public Upgrade parent;
    public Upgrade[] children = new Upgrade[0];
    public String name;
    public boolean exclusive = false;


    public Upgrade(String name, Upgrade[] children, boolean exclusive) {
        this(name,children);
        this.exclusive = exclusive;
    }

    public Upgrade(String name, Upgrade[] children) {
        this.name = name;
        this.children = children;
        for (Upgrade child : children){
            child.setParent(this);
        }
    }

    public Upgrade(String name){
        this.name = name;
    }

    public void onSave(@NotNull NbtCompound nbtCompound, PlayerData plrData){
        NbtCompound nbt = new NbtCompound();
        if(plrData.boughtUpgrades.contains(this)){
            nbt.putBoolean("isActive", plrData.activeUpgrades.contains(this));
            nbtCompound.put(name,nbt);
            for (Upgrade c : children){
                c.onSave(nbtCompound, plrData);
            }
        }

    }

    public void onRead(@NotNull NbtCompound nbtCompound, PlayerData plrData){
        if(nbtCompound.contains(name)){
            NbtCompound nbt = nbtCompound.getCompound(name);
            if(nbt.getBoolean("isActive")){
                plrData.activeUpgrades.add(this);
            }
            plrData.boughtUpgrades.add(this);
            for (Upgrade c : children){
                c.onRead(nbtCompound, plrData);
            }
        }
    }

    public Upgrade[] nextUpgrades(PlayerData plrData){
        if(!plrData.boughtUpgrades.contains(this)){
            return new Upgrade[]{this};
        }
        ArrayList<Upgrade> upgrades = new ArrayList<>();
        for (Upgrade c : children){
            Collections.addAll(upgrades,c.nextUpgrades(plrData));
        }
        return upgrades.toArray(Upgrade[]::new);
    }

    public boolean canBuy(PlayerData plrData){
        if(parent == null || !parent.exclusive){
            return true;
        }
        for (Upgrade child : parent.children){
            if(plrData.boughtUpgrades.contains(child)){
                return false;
            }
        }
        return true;
    }


    public Upgrade getUpgradeByNameRecursive(Upgrade currentUpgrade, String upgradeName) {
        if (currentUpgrade.name.equals(upgradeName)) {
            return currentUpgrade;
        }

        for (Upgrade child : currentUpgrade.children) {
            Upgrade foundUpgrade = getUpgradeByNameRecursive(child, upgradeName);
            if (foundUpgrade != null) {
                return foundUpgrade;
            }
        }

        return null;
    }

    public void setParent(Upgrade parent){
        this.parent = parent;
    }
}
