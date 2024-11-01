package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.common.DirtBottleEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;


public class WaterPouchItem extends Item implements DyeableItem {
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
        NbtCompound tag = itemStack.getOrCreateNbt();
        int count = tag.getInt("custom_model_data");
        setWaterLevel(itemStack,count);
        return count;
    }

    public void setWaterLevel(ItemStack itemStack, int val){
        NbtCompound tag = itemStack.getOrCreateNbt();
        tag.putInt("custom_model_data",val);
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
        return 3 + EnchantmentHelper.getLevel(Elementals.VOLUME_ENCHANTMENT,itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        SapsUtils.addTranslatable(tooltip,"item.elementals.water_pouch.tooltip",getWaterLevel(stack));
    }


}