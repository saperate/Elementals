package dev.saperate.elementals.blocks.blockEntities;

import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.items.IGrillableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrillBlockEntity extends BlockEntity {
    private BlockState campfire = null;
    private ItemStack containedStack = ItemStack.EMPTY;
    /// The time in ticks since we added a stack onto the GrillBlockEntity <br>
    /// This will only go up if canCook returns true
    private int cookTime = 0;

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalsBlocks.GRILL_BLOCK_ENTITY.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, GrillBlockEntity blockEntity) {
        // When there is a hay block below, produce more smoke
        boolean hasHay = level.getBlockState(pos.below()).getBlock() == Blocks.HAY_BLOCK;
        
        if(blockEntity.hasCampfire() && blockEntity.isCampfireLit()){
            RandomSource randomsource = level.random;
            if (randomsource.nextFloat() < (hasHay ? 0.11 : 0.05F)) {
                for (int i = 0; i < (hasHay ? randomsource.nextInt(2) + 2 : 1); i++) {
                    CampfireBlock.makeParticles(level, pos, false, false);
                }
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrillBlockEntity blockEntity) {
        if(level.isClientSide){
            clientTick(level, pos, state, blockEntity);
        }
        if(!blockEntity.canCook() || !(blockEntity.containedStack.getItem() instanceof IGrillableItem grillable)){
            return; // No processing needed if we can't cook
        }
        blockEntity.cookTime++;
        if(!blockEntity.containedStack.isEmpty())
            grillable.grillTick(blockEntity.containedStack, blockEntity.cookTime, level, pos, state, blockEntity);
    }

    /**
     * @return Whether there is an item stack on the grill or not
     */
    public boolean hasContainedStack(){
        return containedStack != null && !containedStack.isEmpty();
    }

    /**
     * Sets the item stack contained within the grill and resets the cookTime. <br>
     * It will throw an error if there is already one placed. <br>
     * To remove the stack contained, use removeStack() <br>
     * It will do nothing if the stack is not placeable on the grill
     * @param stack the item stack to add
     * @return Whether the item stack was placed or not
     */
    public boolean putContainedStack(ItemStack stack){
        if(hasContainedStack()){
            throw new RuntimeException("Tried to put a stack on grill whilst it was full!");
        }
        
        if(isPlaceable(stack.getItem())){
            containedStack = stack.copy();
            containedStack.setCount(1);
            cookTime = 0;
            setChanged();
            return true;
        }
        return false;
    }

    /**
     * Removes the item stack from the grill and resets the cookTime then returns it
     */
    public ItemStack removeContainedStack(){
        ItemStack out = containedStack;
        containedStack = ItemStack.EMPTY;
        cookTime = 0;
        setChanged();
        return out;
    }

    public ItemStack getContainedStack(){
        if(!hasContainedStack()){
            throw new RuntimeException("Tried to get contained stack whilst having none!");
        }
        return containedStack;
    }

    /**
     * @return True if it has a campfire and a block and the campfire is lit
     */
    public boolean canCook(){
        return hasCampfire() && hasContainedStack() && isCampfireLit();
    }

    /**
     * @return Whether there is a campfire on the grill or not
     */
    public boolean hasCampfire(){
        return campfire != null && !campfire.isAir();
    }

    /**
     * Sets the campfire block contained within the grill. <br>
     * It will not set any if there is already one placed. <br>
     * To remove a campfire from the block, use removeCampfire() <br>
     * It will do nothing if the blockstate's block is not a campfire
     * @param campfire the campfire to add
     * @return Whether the campfire was placed or not
     */
    public boolean putCampfire(BlockState campfire){
        if(!hasCampfire() || !(campfire.getBlock() instanceof CampfireBlock)){
            this.campfire = campfire;
            setChanged();
            return true;
        }
        return false;
    }

    /**
     * Removes the campfire from the grill and resets the cookTime then returns it
     */
    public BlockState removeCampfire(){
        BlockState out = campfire;
        campfire = Blocks.AIR.defaultBlockState();
        cookTime = 0;
        setChanged();
        return out;
    }
    
    public void setCampfireLit(boolean val){
        if(campfire == null){
            throw new RuntimeException("Tried to set campfire litness whilst having none!");
        }
        campfire = campfire.setValue(CampfireBlock.LIT, val);
        cookTime = 0;
        setChanged();
    }
    
    public boolean isCampfireLit(){
        if(campfire == null){
            throw new RuntimeException("Tried to get campfire litness whilst having none!");
        }
        return campfire.getValue(CampfireBlock.LIT);
    }
    
    public BlockState getCampfire(){
        if(!hasCampfire()){
            throw new RuntimeException("Tried to get campfire whilst having none!");
        }
        return campfire;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putBoolean("has_campfire_block", hasCampfire());
        if(hasCampfire()) {
            tag.put("campfire_block", NbtUtils.writeBlockState(campfire));
        }

        tag.putBoolean("has_contained_stack", hasContainedStack());
        if(hasContainedStack()) {
            tag.put("contained_stack", containedStack.save(registries));
        }
        
        tag.putInt("cook_time", cookTime);
        
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        // This little variable here is taken from mojang code
        HolderGetter<Block> holdergetter = this.level != null
                ? this.level.holderLookup(Registries.BLOCK)
                : BuiltInRegistries.BLOCK.asLookup();
        
        campfire = tag.getBoolean("has_campfire_block") 
                ? NbtUtils.readBlockState(holdergetter, tag.getCompound("campfire_block"))
                : null;
        containedStack = tag.getBoolean("has_contained_stack")
                ? ItemStack.parse(registries, tag.getCompound("contained_stack")).orElse(ItemStack.EMPTY)
                : null;
        cookTime = tag.getInt("cook_time");
        
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

    @Override
    public void setChanged() {
        super.setChanged();
        level.blockUpdated(getBlockPos(), getBlockState().getBlock());
    }

    /**
     * Checks whether an item can be placed in the grill or not
     */
    public boolean isPlaceable(Item item){ // TODO add a blacklist from config
        return item instanceof IGrillableItem;
    }
}
