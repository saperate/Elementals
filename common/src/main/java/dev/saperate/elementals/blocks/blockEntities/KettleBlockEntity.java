package dev.saperate.elementals.blocks.blockEntities;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class KettleBlockEntity extends BlockEntity {
    private Fluid fluid = Fluids.EMPTY;
    private int fluidLevel = 0;
    private final ItemStack[] ingredients = new ItemStack[9];

    public KettleBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalsBlocks.KETTLE_BLOCK_ENTITY.get(), pos, state);
    }
    
    public void putInSlot(int slot, ItemStack stack){
        ingredients[slot] = stack.copy();
        setChanged();
    }
    
    public void emptySlot(int slot){
        ingredients[slot] = ItemStack.EMPTY;
        setChanged();
    }
    
    public ItemStack getSlot(int slot){
        return ingredients[slot];
    }
    
    public boolean hasItemInSlot(int slot){
        return slot < getMaxSlot() && !ingredients[slot].isEmpty();
    }
    
    public int getMaxSlot(){
        return ingredients.length - 1;
    }
    
    /// Starts at the end of the ingredients list and returns the first one encountered
    /// returns -1 if there was none
    public int getLastUsedSlot(){
        for (int i = ingredients.length - 1; i >= 0 ; i--) {
            if(ingredients[i] != null && !ingredients[i].isEmpty()){
                return i;
            }
        }
        return -1;
    }

    /** Starts at the start of the ingredients list and returns the first empty (or fillable) slot encountered <br>
     * If it is fillable, it will return the slot, even if it cant fit the full count into the slot <br>
     * returns -1 if there was none
    */
    public int getFirstUsableSlot(ItemStack stack){
        for (int i = 0; i < ingredients.length - 1 ; i++) {
            ItemStack curr = ingredients[i];
            if(curr == null || curr.isEmpty() || (curr.is(stack.getItem()) && curr.isStackable() && curr.getCount() < curr.getMaxStackSize())){
                return i;
            }
        }
        return -1;
    }
    
    public ItemStack[] getIngredients(){
        return ingredients;
    }

    /**
     * @return Whether there is a fluid in the kettle or not
     */
    public boolean hasFluid(){
        return fluid != null && !fluid.isSame(Fluids.EMPTY);
    }

    /**
     * @return Whether there is water in the kettle or not
     */
    public boolean hasWater(){
        return hasFluid() && fluid.isSame(Fluids.WATER);
    }

    /**
     * Sets the fluid contained within the kettle<br>
     * If there is already another fluid, it will throw an error <br>
     * To remove the fluid contained, use removeFluid() <br>
     * It will do nothing if the fluid is not water
     * @param stack the item stack to add
     * @return Whether the item stack was placed or not
     */
    public boolean putOrAddFluid(ItemStack stack){
        int itemValue = getItemFluidValue(stack);
        if(hasFluid()){
            if(!hasWater()){
                throw new RuntimeException("Tried to add water to kettle containing other fluid!");
            }
            fluidLevel += itemValue;
            setChanged();
            return true;
        }
        
        fluid = Fluids.WATER;
        fluidLevel = itemValue;
        setChanged();
        return true;
    }

    public ItemStack removeFluidFromStack(ItemStack stack) {
        if(stack.is(Items.WATER_BUCKET)){
            return Items.BUCKET.getDefaultInstance();
        } else if (stack.is(Items.GLASS_BOTTLE)) {
            return Items.GLASS_BOTTLE.getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }
    
    public int getItemFluidValue(ItemStack stack) {
        if(stack.is(Items.WATER_BUCKET)){
            return 15;
        } else if (stack.is(Items.GLASS_BOTTLE)) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if(contents != null && !contents.hasEffects()){
                return 5;
            }
        }
        return 0;
    }

    /**
     * Removes an amount of fluid from the kettle. returns false if empty or if it cannot
     */
    public boolean removeFluid(int amount){
        if(!hasFluid() || amount > fluidLevel){
            return false;
        }
        fluidLevel -= amount;
        if(fluidLevel == 0){
            fluid = Fluids.EMPTY;
        }
        setChanged();
        return true;
    }

    public int getFluidLevel(){
        return !hasFluid() ? 0 : fluidLevel;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putInt("fluid_level", getFluidLevel());
        
        tag.putBoolean("has_fluid", hasFluid());
        if(hasFluid()) {
            tag.put("fluid", NbtUtils.writeFluidState(fluid.defaultFluidState()));
        }
        
        CompoundTag ingredientsTag = new CompoundTag();
        ingredientsTag.putInt("length", ingredients.length);
        for (int i = 0; i < getMaxSlot(); i++) {
            ItemStack ingredient = ingredients[i];
            if(ingredient == null || ingredient.isEmpty()){
                continue;
            }
            ingredientsTag.put(String.valueOf(i), ingredient.save(registries));
        }
        tag.put("ingredients", ingredientsTag);
        
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        // This little variable here is taken from mojang code
        HolderGetter<Fluid> holdergetter = this.level != null
                ? this.level.holderLookup(Registries.FLUID)
                : BuiltInRegistries.FLUID.asLookup();
        
        fluidLevel = tag.getInt("fluid_level");
        fluid = tag.getBoolean("has_fluid") 
                ? SapsUtils.readFluidState(holdergetter, tag.getCompound("fluid")).getType()
                : null;
        
        CompoundTag ingredientsTag = tag.getCompound("ingredients");
        int length = ingredientsTag.getInt("length");
        for (int i = 0; i < length; i++) {
            Tag itemTag = ingredientsTag.get(String.valueOf(i));
            if(itemTag == null){
                continue;
            }
            ItemStack stack = ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY);
            if(i >= ingredients.length){
                Elementals.LOGGER.error("Kettle has smaller size than before! Discarding : {}", stack);
            }
            ingredients[i] = stack;
        }
        
        super.loadAdditional(tag, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
