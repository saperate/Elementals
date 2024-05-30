package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.PlayerData;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public final class Upgrade {
    public Upgrade parent;
    public Upgrade[] children = new Upgrade[0];
    public String name;
    public boolean exclusive = false;

    public Upgrade(String name, Upgrade[] children, boolean exclusive) {
        this(name, children);
        this.exclusive = exclusive;
    }

    public Upgrade(String name, Upgrade[] children) {
        this.name = name;
        this.children = children;
        for (Upgrade child : children) {
            child.setParent(this);
        }
    }

    public Upgrade(String name) {
        this.name = name;
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

        for (Upgrade ignored : children) {
            Upgrade foundUpgrade = getUpgradeByNameRecursive(upgradeName);
            if (foundUpgrade != null) {
                return foundUpgrade;
            }
        }

        return null;
    }

    public void setParent(Upgrade parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
