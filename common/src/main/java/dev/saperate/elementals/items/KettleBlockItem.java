package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.blocks.GrillBlock;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import dev.saperate.elementals.blocks.blockEntities.KettleBlockEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KettleBlockItem extends ItemNameBlockItem implements IGrillableItem {
    public static final int MAX_FLUID_LEVEL = 15;

    public KettleBlockItem(Properties properties) {
        super(ElementalsBlocks.KETTLE_BLOCK.get(), properties);
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        if (!context.getItemInHand().isEmpty()) {

        }
        return super.getPlacementState(context);
    }

    public static ItemStack fromBlockEntity(KettleBlockEntity blockEntity) {
        ItemStack stack = ElementalsItems.KETTLE_ITEM.get().getDefaultInstance();
        KettleBlockItem.setComponentsFromBlockEntity(stack, blockEntity);
        return stack;
    }

    public static void setComponentsFromBlockEntity(ItemStack stack, KettleBlockEntity blockEntity) {
        KettleBlockItem item = (KettleBlockItem) stack.getItem();

        item.setFluid(stack, blockEntity.getFluid());
        item.setFluidLevel(stack, blockEntity.getFluidLevel());
        item.setIngredients(stack, NonNullList.of(ItemStack.EMPTY, blockEntity.getIngredients()));
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setFluid(stack, Fluids.EMPTY);
        setFluidLevel(stack, 0);
        setIngredients(stack, NonNullList.withSize(9, ItemStack.EMPTY));
        return stack;
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

    public void setFluid(ItemStack stack, Fluid fluid) {
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

    public void setIngredients(ItemStack stack, NonNullList<ItemStack> items) {
        ItemContainerContents component = stack.get(DataComponents.CONTAINER);
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    public NonNullList<ItemStack> getIngredients(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemContainerContents component = stack.get(DataComponents.CONTAINER);
        if (component == null) {
            return items;
        }
        component.copyInto(items);
        return items;
    }

    @Override
    public void grillTick(ItemStack stack, int cookTime, Level level, BlockPos pos, BlockState state, GrillBlockEntity blockEntity) {
        Fluid fluid = getFluid(stack, level);
        if (fluid.isSame(Fluids.WATER) && level.isClientSide) {
            Direction dir = GrillBlock.getDirection(blockEntity);
            double rot = Math.toRadians(dir.toYRot());

            Vec3 spoutPos = pos.getBottomCenter().add(
                    -Math.sin(rot) * 0.5f,
                    19 / 16d,
                    Math.cos(rot) * 0.5f
            );

            level.addParticle(Elementals.KETTLE_STEAM_PARTICLE_TYPE,
                    spoutPos.x, spoutPos.y, spoutPos.z,
                    dir.getStepX() * 0.1, 
                    0, 
                    dir.getStepZ() * 0.1
            );
        }
    }

    public int getLastUsedSlot(List<ItemStack> slots) {
        for (int i = slots.size() - 1; i >= 0; i--) {
            if (slots.get(i) != null && !slots.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstUsableSlot(List<ItemStack> slots) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == null || slots.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private boolean putOrAddFluid(ItemStack kettleStack, ItemStack fillStack, Level level) {
        int currFluidLevel = getFluidLevel(kettleStack);
        Fluid currFluid = getFluid(kettleStack, level);

        if (currFluidLevel >= KettleBlockItem.MAX_FLUID_LEVEL) {
            return false;
        }
        int itemValue = KettleBlockEntity.getItemFluidValue(fillStack);
        if (currFluid.isSame(Fluids.WATER)) {
            setFluidLevel(kettleStack, Math.min(KettleBlockItem.MAX_FLUID_LEVEL, currFluidLevel + itemValue));
            return true;
        }

        setFluid(kettleStack, Fluids.WATER);
        setFluidLevel(kettleStack, Math.min(KettleBlockItem.MAX_FLUID_LEVEL, itemValue));
        return true;
    }

    /**
     * Removes an amount of fluid from the kettle. returns false if empty or if it cannot
     */
    private boolean removeFluid(ItemStack kettleStack, Level level, int amount) {
        return removeFluid(kettleStack, level, amount, false);
    }

    /**
     * Removes an amount of fluid from the kettle. returns false if empty or if it cannot
     * if force is set to true, it will remove the fluid even if it doesnt have the full amount
     * It will still return false to indicate it didnt have enough
     */
    private boolean removeFluid(ItemStack kettleStack, Level level, int amount, boolean force) {
        int fluidLevel = getFluidLevel(kettleStack);
        boolean kettleHasEnoughFluid = amount <= fluidLevel;
        if (getFluid(kettleStack, level).isSame(Fluids.EMPTY) || (!kettleHasEnoughFluid && !force)) {
            return false;
        }
        setFluidLevel(kettleStack, Math.max(0, fluidLevel - amount));
        if (Math.max(0, fluidLevel - amount) == 0) {
            setFluid(kettleStack, Fluids.EMPTY);
        }
        return kettleHasEnoughFluid;
    }

    /**
     * Places ingredients into the kettle
     */
    @Override
    public boolean useWithoutItem(ItemStack stack, Level level, BlockPos pos, Player player) {
        NonNullList<ItemStack> ingredients = getIngredients(stack);
        int lastUsedSlot = getLastUsedSlot(ingredients);
        if (lastUsedSlot != -1) {
            SapsUtils.addOrDropItem(player, level, ingredients.get(lastUsedSlot));
            ingredients.set(lastUsedSlot, ItemStack.EMPTY);
            setIngredients(stack, ingredients);
        }
        return true;
    }

    @Override
    public boolean useItemOn(ItemStack kettleStack, Level level, BlockPos pos, Player player, ItemStack itemUsed) {
        NonNullList<ItemStack> ingredients = getIngredients(kettleStack);

        if (tryFillKettle(itemUsed, kettleStack, player, level)) {
            return true;
        } else if (!itemUsed.isEmpty()) { // The item doesn't add any water to our kettle
            int firstUsableSlot = getFirstUsableSlot(ingredients);
            if (firstUsableSlot != -1) {
                ItemStack slotStack = ingredients.get(firstUsableSlot);

                if (slotStack.is(itemUsed.getItem())) { // We have to merge the stacks
                    int totalCount = slotStack.getCount() + itemUsed.getCount();
                    itemUsed.setCount(Math.min(itemUsed.getMaxStackSize(), totalCount));
                    ingredients.set(firstUsableSlot, itemUsed.copy());

                    if (totalCount > itemUsed.getMaxStackSize()) {
                        itemUsed.setCount(totalCount - itemUsed.getMaxStackSize());
                    } else {
                        itemUsed.setCount(0);
                    }

                } else { //The slot is empty, we just set it :)
                    ingredients.set(firstUsableSlot, itemUsed.copyAndClear());
                }
                setIngredients(kettleStack, ingredients);
            }
            return true;
        }
        return false;
    }

    public boolean tryFillKettle(ItemStack fillStack, ItemStack kettleStack, Player player, Level level) {
        Fluid currentFluid = getFluid(kettleStack, level);

        if (KettleBlockEntity.getItemFluidValue(fillStack) != 0) {// It has a water value, try to add some to it
            if (putOrAddFluid(kettleStack, fillStack, level) && (currentFluid.isSame(Fluids.EMPTY) || currentFluid.isSame(Fluids.WATER))) {
                if (!player.isCreative()) {
                    SapsUtils.addOrDropItem(player, level, KettleBlockEntity.removeFluidFromStack(fillStack));
                    fillStack.shrink(1);
                }
            }
            return true;
        } else { // Check if we could fill a container instead (ex: bucket)
            if (fillStack.is(Items.BUCKET)) {
                if (currentFluid.isSame(Fluids.WATER) && removeFluid(kettleStack, level, 15, true) && !player.isCreative()) {
                    SapsUtils.addOrDropItem(player, level, Items.WATER_BUCKET.getDefaultInstance());
                    fillStack.shrink(1);
                }
                return true;
            } else if (fillStack.is(Items.GLASS_BOTTLE)) {
                if (currentFluid.isSame(Fluids.WATER) && removeFluid(kettleStack, level, 5) && !player.isCreative()) {
                    SapsUtils.addOrDropItem(player, level, Items.POTION.getDefaultInstance());
                    fillStack.shrink(1);
                }
                return true;
            }
        }
        return false;
    }
}
