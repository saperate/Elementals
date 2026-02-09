package dev.saperate.elementals.misc;

import dev.saperate.elementals.elements.earth.EarthElement;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
            World world = server.getWorld(entry.worldKey);
            if(world == null)
                continue;

            if(!world.getBlockState(entry.pos).isAir()){
                world.breakBlock(entry.pos,true);
            }
            world.setBlockState(entry.pos, entry.state);
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
        public final RegistryKey<World> worldKey;
        public int lifetime;

        public BlockInformation(BlockPos pos, BlockState state, RegistryKey<World> worldKey, int lifetime) {
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
