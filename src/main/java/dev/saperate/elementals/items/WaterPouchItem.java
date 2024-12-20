package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.enchantments.ElementalsEnchantments;
import dev.saperate.elementals.entities.common.DirtBottleEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Iterator;
import java.util.List;


public class WaterPouchItem extends Item {
    public WaterPouchItem(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        //We have to raycast since this method doesn't check for fluids
        HitResult hit = SapsUtils.raycastFull(context.getPlayer(),20,true);
        if(hit instanceof BlockHitResult bHit
                && context.getWorld().getBlockState(bHit.getBlockPos()).getBlock().equals(Blocks.WATER)){

            fillPouch(context.getStack(),-1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        WaterPouchItem item = (WaterPouchItem) stack.getItem();
        item.setWaterLevel(stack, 0);
        return super.getDefaultStack();
    }

    public int getWaterLevel(ItemStack itemStack){
        NbtComponent data = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (data != null) {
            int count = data.copyNbt().getInt("custom_model_data");
            setWaterLevel(itemStack,count);
            return count;
        }
        return 0;
    }

    public void setWaterLevel(ItemStack itemStack, int val){
        NbtComponent component = itemStack.get(DataComponentTypes.CUSTOM_DATA);

        NbtCompound data;
        if(component != null){
            data = component.copyNbt();
            data.putInt("custom_model_data", val);
        }else {
            data = new NbtCompound();
            data.putInt("custom_model_data", val);
        }

        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
    }

    /**
     * Fills a water pouch by the specified amount of water levels.
     * If the number passed in is negative, it will be filled to max capacity.
     * @return Whether the operation was successful
     */
    public boolean fillPouch(ItemStack itemStack, int amount){
        int max = getMaxWaterLevel(itemStack);
        int level = getWaterLevel(itemStack);

        if(amount < 0 && level <= max){
            setWaterLevel(itemStack,max);
            return true;
        } else if (level + amount <= max) {
            setWaterLevel(itemStack,level + amount);
            return true;
        }
        return false;
    }

    /**
     * Empties a water pouch by the specified amount of water levels.
     * If the number passed in is negative, it will be completely emptied
     * @return Whether the operation was successful
     */
    public boolean emptyPouch(ItemStack itemStack, int amount){
        int level = getWaterLevel(itemStack);

        if(level - amount < 0){
            setWaterLevel(itemStack,0);
            return false;
        } else if (amount < 0 && level > 0) {
            setWaterLevel(itemStack,0);
            return true;
        }else if(level - amount >= 0){
            setWaterLevel(itemStack,level - amount);
            return true;
        }
        return false;
    }

    public int getMaxWaterLevel(ItemStack itemStack){
        int level = 0;
        for (RegistryEntry<Enchantment> enchant : EnchantmentHelper.getEnchantments(itemStack).getEnchantments()){
            if(enchant.getKey().isPresent() && enchant.getKey().get().equals(ElementalsEnchantments.VOLUME)){
                level = EnchantmentHelper.getLevel(enchant, itemStack);
            }
        }

        return 3 + level;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        SapsUtils.addTranslatable(tooltip,"item.elementals.water_pouch.tooltip",getWaterLevel(stack));
    }


    public int getColor(ItemStack stack) {
        return 0xFFFFFFFF;
    }
}