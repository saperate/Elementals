package dev.saperate.elementals.items;

import dev.saperate.elementals.entities.common.DirtBottleEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class WaterPouchItem extends Item {

    public WaterPouchItem(Settings settings) {
        super(settings);
    }


    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        WaterPouchItem item = (WaterPouchItem) stack.getItem();
        item.setWaterLevel(stack, 3);
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

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        SapsUtils.addTranslatable(tooltip,"item.elementals.water_pouch.tooltip",getWaterLevel(itemStack));
    }
}