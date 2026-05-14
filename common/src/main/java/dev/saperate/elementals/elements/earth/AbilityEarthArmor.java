package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.items.EarthArmorItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.items.ElementalsItems.*;
import static dev.saperate.elementals.utils.SapsUtils.raycastBlockCustomRotation;

public class AbilityEarthArmor implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        Player player = bender.player;
        NonNullList<ItemStack> inv = player.getInventory().armor;

        if (player.getInventory().hasAnyOf(getEarthArmorSet())) {
            removeArmorSet(inv);

            player.removeEffect(ElementalsStatusEffects.SEISMIC_SENSE.get());
            player.removeEffect(ElementalsStatusEffects.DENSE.get());
            player.removeEffect(MobEffects.NIGHT_VISION);
            player.removeEffect(MobEffects.BLINDNESS);
            return;
        }

        BlockHitResult hit = raycastBlockCustomRotation(player, 4, true, new Vec3(0, -1, 0));

        if (!EarthElement.isBlockBendable(player.level().getBlockState(hit.getBlockPos()), bender) || !player.onGround()) {
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

        Block standingBlock = player.level().getBlockState(hit.getBlockPos()).getBlock();
        Level world = player.level();

        inv.set(EquipmentSlot.HEAD.getIndex(), EARTH_HELMET.get().getItemStack(inv.get(3), standingBlock, world));
        inv.set(EquipmentSlot.CHEST.getIndex(), EARTH_CHESTPLATE.get().getItemStack(inv.get(2), standingBlock, world));
        inv.set(EquipmentSlot.LEGS.getIndex(), EARTH_LEGGINGS.get().getItemStack(inv.get(1), standingBlock, world));
        inv.set(EquipmentSlot.FEET.getIndex(), EARTH_BOOTS.get().getItemStack(inv.get(0), standingBlock, world));
    }

    public static void removeArmorSet(NonNullList<ItemStack> inv) {
        ItemStack[] armor = getArmorStacks(inv);

        removeArmor(EquipmentSlot.HEAD, armor[0], inv);
        removeArmor(EquipmentSlot.CHEST, armor[1], inv);
        removeArmor(EquipmentSlot.LEGS, armor[2], inv);
        removeArmor(EquipmentSlot.FEET, armor[3], inv);
    }

    public static void removeArmor(EquipmentSlot slot, ItemStack stack, NonNullList<ItemStack> inv) {
        if (!stack.isEmpty() && stack.getItem() instanceof EarthArmorItem) {
            ItemStack item = EarthArmorItem.getItem(stack);
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
