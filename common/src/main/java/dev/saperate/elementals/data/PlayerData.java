package dev.saperate.elementals.data;

import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import dev.saperate.elementals.network.packets.common.SyncLevelPacket;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerData {
    public Ability[] boundAbilities = new Ability[4];
    //the upgrades in these are incomplete, meaning that most methods in them won't work because they lack
    //parents & children. However, they are equal to their complete counterparts so use that if you need the methods.
    public HashMap<Upgrade, Boolean> upgrades = new HashMap<>();
    public ArrayList<Element> elements = new ArrayList<>();
    public int activeElementIndex = 0;
    public float chi = 100, xp = 0;
    public int level = 2;

    public PlayerData() {
        elements.add(NoneElement.get());
    }

    /**
     * Checks if the player both has the upgrade AND has it enabled.
     * @param upgradeName The name of the upgrade we are checking
     * @return if the player can use the upgrade
     */
    public boolean canUseUpgrade(String upgradeName) {
        Upgrade key = new Upgrade(upgradeName, 0);
        return upgrades.getOrDefault(key, false);
    }

    /**
     * This does not automatically sync the level of the player,
     * to do that, use the send method in SyncLevelC2SPacket.
     * @param upgrade The upgrade we want to buy
     * @return True if we were able to buy the upgrade, False if not
     * @see SyncLevelPacket
     */
    public boolean buyUpgrade(Upgrade upgrade) {
        //Wrapper class so that we can reduce the levels
        AtomicInteger lvl = new AtomicInteger(level);

        boolean bought = canBuyUpgrade(upgrades, elements.get(activeElementIndex), upgrade.name, lvl);
        if (bought) {
            upgrades.put(upgrade, true);
            level -= upgrade.price;
        }

        return bought;
    }

    public static boolean canBuyUpgrade(HashMap<Upgrade, Boolean> plrUpgrades, Element element, String upgradeName, AtomicInteger level) {
        for (Upgrade branches : element.root.children) {
            for (Upgrade upgrade : branches.nextUpgrades(plrUpgrades)) {
                if (upgrade.name.equals(upgradeName) && upgrade.canBuy(plrUpgrades)) {
                    return level.get() >= upgrade.price;
                }
            }
        }
        return false;
    }

    /**
     * Toggles an upgrade. 
     * Making it enabled will allow it to affect bending, disabled will make it act as if it was not bought.
     * It takes into account exclusive upgrades
     * Does nothing if it is missing
     * @param upgrade The upgrade to toggle
     */
    public void toggleUpgrade(Upgrade upgrade) {
        if (upgrades.containsKey(upgrade)) {
            setUpgrade(upgrade, !upgrades.get(upgrade));
        }
    }

    /**
     * Changes the status of an upgrade between on and off. 
     * Disabled, the upgrade will act as if it was not bought
     * It takes into account exclusive upgrades
     * Does nothing if it is missing or if the parent is disabled (or missing)
     * @param upgrade The upgrade to change the status of
     * @param val True if enabling, False if disabling
     */
    public void setUpgrade(Upgrade upgrade, boolean val){
        if (upgrades.containsKey(upgrade)) {
            if(!upgrades.getOrDefault(upgrade.parent, false) && upgrade.parent.parent != null)
                return;
            
            upgrades.put(upgrade, val);
            fixUpgradeChildrenRecursive(upgrade,val);

            if(val && upgrade.parent.exclusive){ //Fixes the siblings
                fixExclusiveUpgrades(upgrade);
            }
        }
    }
    
    
    public void fixUpgradeChildrenRecursive(Upgrade root, boolean enabled){
        if(root.exclusive && enabled)
            return;
        
        Stack<Upgrade> disableStack = new Stack<>();
        disableStack.addAll(List.of(root.children));

        while(!disableStack.empty()){
            Upgrade curr = disableStack.pop();
            if(!upgrades.containsKey(curr))
                continue;
            upgrades.put(curr, enabled);
            
            if(!enabled || !curr.exclusive)
                disableStack.addAll(List.of(curr.children));
        }
    }
    

    /**
     * Fixes the skill tree after we set an exclusive upgrade's status
     * @param except The upgrade to ignore
     */
    private void fixExclusiveUpgrades(@NotNull Upgrade except){
        Stack<Upgrade> disableStack = new Stack<>();
        disableStack.addAll(List.of(except.parent.children));

        while(!disableStack.empty()){
            Upgrade curr = disableStack.pop();
            if(curr == except || !upgrades.containsKey(curr))
                continue;
            disableStack.addAll(List.of(curr.children));
            upgrades.put(curr, false);
        }
    }
    
    
    /**
     * @return The current active element
     */
    public Element getElement() {
        if(elements.isEmpty()){
            return NoneElement.get();
        }
        return elements.get(activeElementIndex);
    }

    public static PlayerData get(Player player) {
        return StateDataSaverAndLoader.getPlayerState(player);
    }
}
