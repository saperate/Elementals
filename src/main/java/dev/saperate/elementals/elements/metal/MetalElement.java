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
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.isBeingRainedOn;

public class MetalElement extends Element {
    
    public MetalElement() {
        super("Metal", new Upgrade("Metal", new  Upgrade[]{

        },0));
        addAbility(new AbilityMetal1(), true);
        addAbility(new AbilityMetalBullets());
        addAbility(new AbilityMetalLance());
        addAbility(new AbilityMetal2(), true);
        addAbility(new AbilityMetalBind());
        addAbility(new AbilityMetal3(),true);
        addAbility(new AbilityMetalCable());
        addAbility(new AbilityMetal4(),true);
        addAbility(new AbilityMetalArmor());
        addAbility(new AbilityMetalDecoy());
    }

    /**
     * This method checks if the player has the metal required to cast an ability. 
     * It <b>WILL</b> take the metal from the inventory if it is able to.
     * @param cost The cost, in iron nuggets, to be able to bend
     * @return True if the player has had metal taken from their inventory
     */
    //TODO Refactor this monolith
    public static boolean canBend(PlayerEntity player, int cost) {
        ElementalConfig config = ElementalConfig.get();
        Inventory inventory = player.getInventory();

        //Save all the slots that have a valid payment
        ArrayList<Integer> validPaymentSlots = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if(config.METAL_COST_VALUE.containsKey(stack.getItem())){
                validPaymentSlots.add(i);
            }
        }
        if(validPaymentSlots.isEmpty())// We can't pay, thus cannot bend
            return false;

        Map<Integer, Integer> slotsUsed = new HashMap<>();//Slot, amount taken
        
        int costLeft = cost;
        while(costLeft > 0){
            int lowestValue = Integer.MAX_VALUE;
            int lowestSlot = -1;
            
            //Find the lowest value slot, we start by taking from that
            for(Integer slot : validPaymentSlots){
                int currValue = config.METAL_COST_VALUE.get(inventory.getStack(slot).getItem());
                if(currValue < lowestValue){
                    lowestValue = currValue;
                    lowestSlot = slot;
                }
            }
            if(lowestSlot == -1) //Could not find the lowest value slot, no slot left, we didn't have enough
                return false;
            
            
            ItemStack lowestStack = inventory.getStack(lowestSlot);
            int value = config.METAL_COST_VALUE.get(lowestStack.getItem());
            if(value > costLeft){//try to break the item into smaller parts, otherwise just pay directly
                Item replacement = config.METAL_LOWER_VALUE_STACK.get(lowestStack.getItem());
                
                if(replacement == null){//We had nothing to break it into, and have the smallest value possible
                    costLeft -= value;
                    slotsUsed.put(lowestSlot,1);
                }else{ //Try to break into smaller parts
                    int smallerValue = config.METAL_COST_VALUE.get(replacement);//The value of the broken up item
                    int smallerCount = (int) Math.ceil((double) value / smallerValue);//How much it broke into
                    
                    int amount = (int) Math.ceil((double) costLeft / smallerValue);//How much of the item to satisfy costLeft
                    int amountTaken = Math.min(amount, smallerCount);//If the amount > count, go with count
                    costLeft -= amountTaken * smallerValue;
                    slotsUsed.put(lowestSlot, 1);
                    
                    ItemStack replacementStack = replacement.getDefaultStack();
                    replacementStack.setCount(smallerCount - amountTaken);
                    if(!player.getInventory().insertStack(replacementStack)){
                        //We couldnt insert in the inventory, drop to the ground
                        player.getWorld().spawnEntity(new ItemEntity(
                                player.getWorld(),
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                replacementStack
                        ));
                    }
                    
                }
            }else{
                int amount = (int) Math.ceil((double) costLeft / value);//How much of the item to satisfy costLeft
                int amountTaken = Math.min(amount, lowestStack.getCount());//If the amount > count, go with count
                costLeft -= amountTaken * value;
                slotsUsed.put(lowestSlot, amountTaken);
            }
            
            
        }
        
        
        //Remove every item that was consumed
        for (Map.Entry<Integer, Integer> entry : slotsUsed.entrySet()) {
            ItemStack stack = inventory.getStack(entry.getKey());
            stack.setCount(stack.getCount() - entry.getValue());
            
            if(stack.getCount() <= 0){
                stack = ItemStack.EMPTY;
            }
            
            inventory.setStack(entry.getKey(), stack);
        }
        
        
        return true;
    }
    
    
    public static Element get() {
        return getElement("Metal");
    }

    @Override
    public int getColor() {
        return 0xFFDADDE1;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFF919191;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        return bender.hasElement(this);
    }//TODO make an algorithm for this it's annoying to do by hand
}
