package dev.saperate.elementals.blocks.blockEntities;

import dev.saperate.elementals.blocks.AbstractPieBlock;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.blocks.UncookedPieBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GrillBlockEntity extends BlockEntity {
    private BlockState campfire = null;
    private BlockState containedBlock = Blocks.AIR.defaultBlockState();
    /// The time in ticks since we added a block onto the GrillBlockEntity <br>
    /// This will only go up if canCook returns true
    private int cookTime = 0;

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalsBlocks.GRILL_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrillBlockEntity blockEntity) {
        if(!blockEntity.canCook()){
            return; // No processing needed if we can't cook
        }
        blockEntity.cookTime++;
        System.out.println("cooking! "+ blockEntity.cookTime + " : " + blockEntity.containedBlock.getBlock().getName());
        if (blockEntity.containedBlock.getBlock() instanceof UncookedPieBlock && blockEntity.cookTime >= 300){
            System.out.println("cooked!");
            blockEntity.cookTime = 0;
            // Basically just sets the pie to be cooked while keeping the same pie type
            blockEntity.containedBlock = AbstractPieBlock.getCookedBlockstate(blockEntity.containedBlock.getValue(AbstractPieBlock.PIE_TYPE));
            setChanged(level, pos, state);
        }
    }

    /**
     * @return Whether there is a block on the grill or not
     */
    public boolean hasBlock(){
        return containedBlock != null;
    }

    /**
     * Sets the block contained within the grill and resets the cookTime. <br>
     * It will not set any if there is already one placed. <br>
     * To remove the block contained, use removeBlock() <br>
     * It will do nothing if the block is not placeable on the grill
     * @param block the block to add
     * @return Whether the block was placed or not
     */
    public boolean addBlock(BlockState block){
        if(!hasBlock() && isPlaceable(block.getBlock())){
            containedBlock = block;
            cookTime = 0;
            setChanged();
            return true;
        }
        return false;
    }

    /**
     * Removes the block from the grill and resets the cookTime then returns it
     */
    public BlockState removeBlock(){
        BlockState out = containedBlock;
        containedBlock = null;
        cookTime = 0;
        setChanged();
        return out;
    }
    

    /**
     * @return True if it has a campfire and a block and the campfire is lit
     */
    public boolean canCook(){
        return hasCampfire() && hasBlock() && isCampfireLit();
    }

    /**
     * @return Whether there is a campfire on the grill or not
     */
    public boolean hasCampfire(){
        return campfire != null;
    }

    /**
     * Sets the campfire block contained within the grill. <br>
     * It will not set any if there is already one placed. <br>
     * To remove a campfire from the block, use removeCampfire() <br>
     * It will do nothing if the blockstate's block is not a campfire
     * @param campfire the campfire to add
     * @return Whether the campfire was placed or not
     */
    public boolean addCampfire(BlockState campfire){
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
        campfire = null;
        cookTime = 0;
        setChanged();
        return out;
    }
    
    public void setCampfireLit(boolean val){
        campfire = campfire.setValue(CampfireBlock.LIT, val);
        cookTime = 0;
        setChanged();
    }
    
    public boolean isCampfireLit(){
        return campfire.getValue(CampfireBlock.LIT);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putBoolean("has_campfire_block", hasCampfire());
        if(hasCampfire()) {
            tag.put("campfire_block", NbtUtils.writeBlockState(campfire));
        }

        tag.putBoolean("has_contained_block", hasBlock());
        if(hasBlock()) {
            tag.put("contained_block", NbtUtils.writeBlockState(containedBlock));
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
        containedBlock = tag.getBoolean("has_contained_block")
                ? NbtUtils.readBlockState(holdergetter, tag.getCompound("contained_block"))
                : null;
        cookTime = tag.getInt("cook_time");
        
        super.loadAdditional(tag, registries);
    }

    /**
     * Checks whether a block can be placed in the grill or not
     */
    public boolean isPlaceable(Block block){
        return block instanceof AbstractPieBlock;
    }
}
