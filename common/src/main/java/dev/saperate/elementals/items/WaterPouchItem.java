package dev.saperate.elementals.items;


import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.Properties;


public class WaterPouchItem extends Item {
    public WaterPouchItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        //We have to raycast since this method doesn't check for fluids
        HitResult hit = SapsUtils.raycastFull(context.getPlayer(),20,true);
        if(hit instanceof BlockHitResult bHit
                && context.getLevel().getBlockState(bHit.getBlockPos()).getBlock().equals(Blocks.WATER)){

            fillPouch(context.getItemInHand(),-1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        WaterPouchItem item = (WaterPouchItem) stack.getItem();
        item.setWaterLevel(stack, 0);
        return stack;
    }

    public int getWaterLevel(ItemStack itemStack){
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            int count = data.copyTag().getInt("custom_model_data");
            //TODO uncomment if it somehow breaks but idk why this was here
            //setWaterLevel(itemStack,count);
            return count;
        }
        return 0;
    }

    public void setWaterLevel(ItemStack itemStack, int val){
        CustomData component = itemStack.get(DataComponents.CUSTOM_DATA);

        CompoundTag data;
        if(component != null){
            data = component.copyTag();
            data.putInt("custom_model_data", val);
        }else {
            data = new CompoundTag();
            data.putInt("custom_model_data", val);
        }

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
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
        
        level = EnchantmentHelper.getItemEnchantmentLevel(ElementalsEnchantments.VOLUME, itemStack);
        return 9 + level * 4;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        SapsUtils.addTranslatable(tooltip,"item.elementals.water_pouch.tooltip",getWaterLevel(stack));
    }


    public int getColor(ItemStack stack) {
        return FastColor.ARGB32.color(
                0xFF, stack.getOrDefault(DataComponents.DYED_COLOR,new DyedItemColor(0x4f341d,false)).rgb()
        );
    }
}