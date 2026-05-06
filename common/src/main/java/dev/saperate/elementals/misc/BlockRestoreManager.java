package dev.saperate.elementals.misc;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class BlockRestoreManager {
    private static final HashSet<BlockInformation> blocksToRestore = new HashSet<>();
    
    public static void tick(MinecraftServer server) {
        List<BlockInformation> toRemove = new ArrayList<>();
        for (BlockInformation entry : blocksToRestore) {
            if(entry.lifetime > 0) {
                entry.lifetime--;
                continue;
            }

            toRemove.add(entry);
            Level world = server.getLevel(entry.worldKey);
            if(world == null)
                continue;

            if(!world.getBlockState(entry.pos).isAir()){
                world.destroyBlock(entry.pos,true);
            }
            world.setBlockAndUpdate(entry.pos, entry.state);
        }
        toRemove.forEach(blocksToRestore::remove);
    }
    
    public static void addBlockToRestore(BlockInformation info){
        blocksToRestore.add(info);
    }
    
    
    
    public static void reset() {
        blocksToRestore.clear();
    }


    public static class BlockInformation{
        public final BlockPos pos;
        public final BlockState state;
        public final ResourceKey<Level> worldKey;
        public int lifetime;

        public BlockInformation(BlockPos pos, BlockState state, ResourceKey<Level> worldKey, int lifetime) {
            this.pos = pos;
            this.state = state;
            this.worldKey = worldKey;
            this.lifetime = lifetime;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            BlockInformation that = (BlockInformation) object;
            return Objects.equals(pos, that.pos) && Objects.equals(state, that.state) && Objects.equals(worldKey, that.worldKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, state, worldKey);
        }
    }
}
