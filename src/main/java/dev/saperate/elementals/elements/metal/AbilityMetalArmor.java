package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.earth.EarthElement;
import dev.saperate.elementals.items.MetalArmorItem;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.items.ElementalItems.*;
import static dev.saperate.elementals.utils.SapsUtils.raycastBlockCustomRotation;

public class AbilityMetalArmor implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        PlayerEntity player = bender.player;
        DefaultedList<ItemStack> inv = player.getInventory().armor;

        if (player.getInventory().containsAny(METAL_ARMOR_SET)) {
            
            removeArmorSet(inv);
            player.removeStatusEffect(SEISMIC_SENSE_EFFECT);
            player.removeStatusEffect(DENSE_EFFECT);
            player.removeStatusEffect(StatusEffects.SLOWNESS);
            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            player.removeStatusEffect(StatusEffects.BLINDNESS);
            return;
        }
        
        float cost = bender.plrData.canUseUpgrade("metalArmorEfficiencyI") ? 20 : 30;
        if (!bender.plrData.canUseUpgrade("metalArmor") ||
                !bender.reduceChi(cost) || !MetalElement.canBend(player,54)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        inv.set(EquipmentSlot.HEAD.getEntitySlotId(), METAL_HELMET.getItemStack(inv.get(3), 0xFFFFFF));
        inv.set(EquipmentSlot.CHEST.getEntitySlotId(), METAL_CHESTPLATE.getItemStack(inv.get(2), 0xFFFFFF));
        inv.set(EquipmentSlot.LEGS.getEntitySlotId(), METAL_LEGGINGS.getItemStack(inv.get(1), 0xFFFFFF));
        inv.set(EquipmentSlot.FEET.getEntitySlotId(), METAL_BOOTS.getItemStack(inv.get(0), 0xFFFFFF));


    }

    public static void removeArmorSet(DefaultedList<ItemStack> inv) {
        ItemStack[] armor = getArmorStacks(inv);

        removeArmor(EquipmentSlot.HEAD, armor[0], inv);
        removeArmor(EquipmentSlot.CHEST, armor[1], inv);
        removeArmor(EquipmentSlot.LEGS, armor[2], inv);
        removeArmor(EquipmentSlot.FEET, armor[3], inv);
    }

    public static void removeArmor(EquipmentSlot slot, ItemStack stack, DefaultedList<ItemStack> inv) {
        if (!stack.isEmpty() && stack.getItem() instanceof MetalArmorItem) {
            ItemStack item = MetalArmorItem.getBundledStacks(stack).findFirst().orElse(ItemStack.EMPTY);
            inv.set(slot.getEntitySlotId(), item);
        }
    }

    public static ItemStack[] getArmorStacks(DefaultedList<ItemStack> inv) {
        return new ItemStack[]{
                inv.get(EquipmentSlot.HEAD.getEntitySlotId()),
                inv.get(EquipmentSlot.CHEST.getEntitySlotId()),
                inv.get(EquipmentSlot.LEGS.getEntitySlotId()),
                inv.get(EquipmentSlot.FEET.getEntitySlotId())
        };
    }
    

    @Override
    public void onRemove(Bender bender) {

    }

}
