package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.isBeingRainedOn;

public class MetalElement extends Element {

    public MetalElement() {
        super("Metal", new Upgrade("Metal", new Upgrade[]{
                new Upgrade("metalBullet", new Upgrade[]{
                        new Upgrade("metalBulletCountI", new Upgrade[]{
                                new Upgrade("metalBulletScatterShotI", 2),
                                new Upgrade("metalBulletCountII", 2),
                                new Upgrade("metalBulletDamageI", new Upgrade[]{
                                        new Upgrade("metalLance", new Upgrade[]{
                                                new Upgrade("metalLanceDamageI", new Upgrade[]{
                                                        new Upgrade("metalLanceDamageII", 2)
                                                }, 2),
                                                new Upgrade("metalLanceRedirectI", 2)
                                        }, 4)
                                }, 2),
                        }, 2)
                }, 4),
                new Upgrade("metalBind", new Upgrade[]{
                        new Upgrade("metalBindRangeI", 2),
                        new Upgrade("metalBindEfficiencyI", new Upgrade[]{
                                new Upgrade("metalBindEfficiencyII", 2)
                        }, 2)
                }, 4),
                new Upgrade("metalCable", new Upgrade[]{
                        new Upgrade("metalCableRangeI", new Upgrade[]{
                                new Upgrade("metalCableSpeedI", new Upgrade[]{
                                        new Upgrade("metalCableSpeedII", 2),
                                        new Upgrade("metalCablePrecisionI", 2)
                                }, 2),
                        }, 2)
                }, 4),
                new Upgrade("metalArmor", new Upgrade[]{
                        new Upgrade("metalArmorEfficiencyI", new Upgrade[]{
                                new Upgrade("metalDecoy", new Upgrade[]{
                                        new Upgrade("metalDecoyRangeI", new Upgrade[]{
                                                new Upgrade("metalDecoyRangeII", 2),
                                                new Upgrade("metalDecoyDamageI", new Upgrade[]{
                                                        new Upgrade("metalDecoyDamageII", 2)
                                                }, 2)
                                        }, 2)
                                }, 6)
                        }, 2)
                }, 4)
        }, 0));
        addAbility(new AbilityMetal1(), true);
        addAbility(new AbilityMetalBullets());
        addAbility(new AbilityMetalLance());
        addAbility(new AbilityMetal2(), true);
        addAbility(new AbilityMetalBind());
        addAbility(new AbilityMetal3(), true);
        addAbility(new AbilityMetalCable());
        addAbility(new AbilityMetal4(), true);
        addAbility(new AbilityMetalArmor());
        addAbility(new AbilityMetalDecoy());
    }

    /**
     * This method checks if the player has the metal required to cast an ability. 
     * It <b>WILL</b> take the metal from the inventory if it is able to.
     * If the player is in creative, it will always return true.
     * @param cost The cost, in iron nuggets, to be able to bend
     * @return True if the player has had metal taken from their inventory
     */
    //TODO Refactor this monolith
    public static boolean canBend(PlayerEntity player, int cost) {
        if (player.getAbilities().creativeMode) {
            return true;
        }

        ElementalConfig config = ElementalConfig.get();
        Inventory inventory = player.getInventory();

        //Save all the slots that have a valid payment
        ArrayList<Integer> validPaymentSlots = getValidPaymentSlots(inventory, config);
        if (validPaymentSlots.isEmpty())// We can't pay, thus cannot bend
            return false;

        Map<Integer, Integer> slotsUsed = new HashMap<>();//Slot, amount taken
        int costLeft = cost;
        while (costLeft > 0) {
            int lowestValue = Integer.MAX_VALUE;
            int lowestSlot = -1;

            //Find the lowest value slot, we start by taking from that
            lowestSlot = getLowestSlot(validPaymentSlots, config, inventory, lowestValue, lowestSlot);
            if (lowestSlot == -1) //Could not find the lowest value slot, no slot left, we didn't have enough
                return false;


            ItemStack lowestStack = inventory.getStack(lowestSlot);
            int itemValue = config.METAL_COST_VALUE.get(lowestStack.getItem());
            int itemAmountNeeded = (int) Math.ceil((double) costLeft / itemValue);//How much of the item to satisfy costLeft
            Item itemReplacement = config.METAL_LOWER_VALUE_STACK.get(lowestStack.getItem());

            if (itemReplacement == null) {//We had nothing to break it into
                int itemAmountToRemove = Math.min(itemAmountNeeded, lowestStack.getCount());//If the amount > count, go with count
                costLeft -= itemAmountToRemove * itemValue;
                slotsUsed.put(lowestSlot, itemAmountToRemove);

            } else {
                int itemReplacementValue = config.METAL_COST_VALUE.get(itemReplacement);
                int itemReplacementCount = (int) Math.ceil((double) itemValue / itemReplacementValue)
                        * Math.min(lowestStack.getCount(), itemAmountNeeded);

                //How much of the item's replacement to satisfy costLeft
                int itemReplacementAmountNeeded = (int) Math.ceil((double) costLeft / itemReplacementValue);
                int itemAmountToRemove = Math.min(itemReplacementAmountNeeded, itemReplacementCount);//If the amount > count, go with count
                costLeft -= itemAmountToRemove * itemReplacementValue;
                slotsUsed.put(lowestSlot, itemAmountNeeded);

                ItemStack replacementStack = itemReplacement.getDefaultStack();
                replacementStack.setCount(itemReplacementCount - itemAmountToRemove);

                //We couldn't insert in the inventory, drop to the ground
                if (!player.getInventory().insertStack(replacementStack))
                    player.getWorld().spawnEntity(new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), replacementStack));
            }
            validPaymentSlots.remove((Object) lowestSlot);

        }

        //Remove every item that was consumed
        removeConsumedItems(slotsUsed, inventory);
        return true;
    }

    private static void removeConsumedItems(Map<Integer, Integer> slotsUsed, Inventory inventory) {
        for (Map.Entry<Integer, Integer> entry : slotsUsed.entrySet()) {
            ItemStack stack = inventory.getStack(entry.getKey());
            stack.setCount(stack.getCount() - entry.getValue());

            if (stack.getCount() <= 0) {
                stack = ItemStack.EMPTY;
            }

            inventory.setStack(entry.getKey(), stack);
        }
    }

    @NotNull
    private static ArrayList<Integer> getValidPaymentSlots(Inventory inventory, ElementalConfig config) {
        ArrayList<Integer> validPaymentSlots = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (config.METAL_COST_VALUE.containsKey(stack.getItem())) {
                validPaymentSlots.add(i);
            }
        }
        return validPaymentSlots;
    }

    private static int getLowestSlot(ArrayList<Integer> validPaymentSlots, ElementalConfig config, Inventory inventory, int lowestValue, int lowestSlot) {
        for (Integer slot : validPaymentSlots) {
            int currValue = config.METAL_COST_VALUE.get(inventory.getStack(slot).getItem());
            if (currValue < lowestValue) {
                lowestValue = currValue;
                lowestSlot = slot;
            }
        }
        return lowestSlot;
    }


    public static Element get() {
        return getElement("Metal");
    }

    @Override
    public int getColor() {
        return 0xFFd4c9c4;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFF8d7c76;
    }

    @Override
    public int getTertiaryColor() {
        return 0xFF4f3933;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        return bender.hasElement(this);
    }//TODO make an algorithm for this it's annoying to do by hand


    @Override
    public String[] getBackgroundTextures() {
        return new String[]{"bottom.png"};
    }
}
