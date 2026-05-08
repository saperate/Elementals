package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.items.MetalArmorItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static dev.saperate.elementals.items.ElementalsItems.*;

public class AbilityMetalArmor implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        Player player = bender.player;
        NonNullList<ItemStack> inv = player.getInventory().armor;

        if (player.getInventory().hasAnyOf(METAL_ARMOR_SET)) {
            
            removeArmorSet(inv);
            player.removeEffect(ElementalsStatusEffects.SEISMIC_SENSE);
            player.removeEffect(ElementalsStatusEffects.DENSE);
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            player.removeEffect(MobEffects.NIGHT_VISION);
            player.removeEffect(MobEffects.BLINDNESS);
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

        Level world = bender.player.level();
        inv.set(EquipmentSlot.HEAD.getIndex(), METAL_HELMET.getItemStack(inv.get(3), 0xFFFFFF, world));
        inv.set(EquipmentSlot.CHEST.getIndex(), METAL_CHESTPLATE.getItemStack(inv.get(2), 0xFFFFFF, world));
        inv.set(EquipmentSlot.LEGS.getIndex(), METAL_LEGGINGS.getItemStack(inv.get(1), 0xFFFFFF, world));
        inv.set(EquipmentSlot.FEET.getIndex(), METAL_BOOTS.getItemStack(inv.get(0), 0xFFFFFF, world));


    }

    public static void removeArmorSet(NonNullList<ItemStack> inv) {
        ItemStack[] armor = getArmorStacks(inv);

        removeArmor(EquipmentSlot.HEAD, armor[0], inv);
        removeArmor(EquipmentSlot.CHEST, armor[1], inv);
        removeArmor(EquipmentSlot.LEGS, armor[2], inv);
        removeArmor(EquipmentSlot.FEET, armor[3], inv);
    }

    public static void removeArmor(EquipmentSlot slot, ItemStack stack, NonNullList<ItemStack> inv) {
        if (!stack.isEmpty() && stack.getItem() instanceof MetalArmorItem) {
            ItemStack item = MetalArmorItem.getItem(stack);
            inv.set(slot.getIndex(), item);
        }
    }

    public static ItemStack[] getArmorStacks(NonNullList<ItemStack> inv) {
        return new ItemStack[]{
                inv.get(EquipmentSlot.HEAD.getIndex()),
                inv.get(EquipmentSlot.CHEST.getIndex()),
                inv.get(EquipmentSlot.LEGS.getIndex()),
                inv.get(EquipmentSlot.FEET.getIndex())
        };
    }
    

    @Override
    public void onRemove(Bender bender) {

    }

}
