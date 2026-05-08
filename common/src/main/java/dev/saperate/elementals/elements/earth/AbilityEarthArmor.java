package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.items.EarthArmorItem;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.MobEffects;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;

import static dev.saperate.elementals.items.ElementalItems.*;
import static dev.saperate.elementals.utils.SapsUtils.raycastBlockCustomRotation;

public class AbilityEarthArmor implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        Player player = bender.player;
        DefaultedList<ItemStack> inv = player.getInventory().armor;

        if (player.getInventory().containsAny(EARTH_ARMOR_SET)) {
            removeArmorSet(inv);

            player.removeStatusEffect(ElementalsStatusEffects.SEISMIC_SENSE);
            player.removeStatusEffect(ElementalsStatusEffects.DENSE);
            player.removeStatusEffect(MobEffects.NIGHT_VISION);
            player.removeStatusEffect(MobEffects.BLINDNESS);
            return;
        }

        BlockHitResult hit = raycastBlockCustomRotation(player, 4, true, new Vec3(0, -1, 0));

        if (!EarthElement.isBlockBendable(player.level().getBlockState(hit.getOnPos()), bender) || !player.isOnGround()) {
            return;
        }
        if (!bender.reduceChi(30)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Block standingBlock = player.level().getBlockState(hit.getOnPos()).getBlock();
        Level world = player.level();

        inv.set(EquipmentSlot.HEAD.getEntitySlotId(), EARTH_HELMET.getItemStack(inv.get(3), standingBlock, world));
        inv.set(EquipmentSlot.CHEST.getEntitySlotId(), EARTH_CHESTPLATE.getItemStack(inv.get(2), standingBlock, world));
        inv.set(EquipmentSlot.LEGS.getEntitySlotId(), EARTH_LEGGINGS.getItemStack(inv.get(1), standingBlock, world));
        inv.set(EquipmentSlot.FEET.getEntitySlotId(), EARTH_BOOTS.getItemStack(inv.get(0), standingBlock, world));
    }

    public static void removeArmorSet(DefaultedList<ItemStack> inv) {
        ItemStack[] armor = getArmorStacks(inv);

        removeArmor(EquipmentSlot.HEAD, armor[0], inv);
        removeArmor(EquipmentSlot.CHEST, armor[1], inv);
        removeArmor(EquipmentSlot.LEGS, armor[2], inv);
        removeArmor(EquipmentSlot.FEET, armor[3], inv);
    }

    public static void removeArmor(EquipmentSlot slot, ItemStack stack, DefaultedList<ItemStack> inv) {
        if (!stack.isEmpty() && stack.getItem() instanceof EarthArmorItem) {
            ItemStack item = EarthArmorItem.getItem(stack);
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
