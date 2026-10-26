package dev.saperate.elementals.items;

import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.blocks.blockEntities.KettleBlockEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class KettleBlockItem extends ItemNameBlockItem implements IGrillableItem {
    public static final int MAX_FLUID_LEVEL = 15;

    public KettleBlockItem(Properties properties) {
        super(ElementalsBlocks.KETTLE_BLOCK.get(), properties);
    }

    public static ItemStack fromBlockEntity(KettleBlockEntity blockEntity) {
        return ElementalsItems.KETTLE_ITEM.get().getDefaultInstance(); // TODO make it add the ingredients & fluid to the stack
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    public void setFluidLevel(ItemStack stack, int level) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);

        CompoundTag data;
        if (component != null) {
            data = component.copyTag();
        } else {
            data = new CompoundTag();
        }
        data.putInt("fluid_level", Math.clamp(level, 0, MAX_FLUID_LEVEL));

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    public int getFluidLevel(ItemStack stack) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);

        CompoundTag data;
        if (component != null) {
            data = component.copyTag();
            return data.getInt("fluid_level");
        } else {
            return 0;
        }
    }
    
    public void setFluid(ItemStack stack, Fluid fluid){
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);

        CompoundTag data;
        if (component != null) {
            data = component.copyTag();
        } else {
            data = new CompoundTag();
        }
        data.put("fluid", NbtUtils.writeFluidState(fluid.defaultFluidState()));

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    public Fluid getFluid(ItemStack stack, Level level) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);

        CompoundTag data;
        if (component != null) {
            data = component.copyTag();
            return SapsUtils.readFluidState(level.holderLookup(Registries.FLUID), data.getCompound("fluid")).getType();
        } else {
            return Fluids.EMPTY;
        }
    }

    public void setIngredients(ItemStack stack, List<ItemStack> items) {
        ItemContainerContents component = stack.get(DataComponents.CONTAINER);
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    public List<ItemStack> getIngredients(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.createWithCapacity(9);
        ItemContainerContents component = stack.get(DataComponents.CONTAINER);
        component.copyInto(items);
        return items;
    }
}
