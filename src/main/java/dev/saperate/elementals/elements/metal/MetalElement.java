package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

import java.util.HashMap;

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
    public static boolean canBend(PlayerEntity player, int cost) {
        
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
