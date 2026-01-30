package dev.saperate.elementals.misc;

import dev.saperate.elementals.elements.earth.EarthElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BlockRestoreManager {
    private static final HashSet<EarthElement.BlockInformation> blocksToRestore = new HashSet<>();
    
    public static void tick(MinecraftServer server) {
        List<EarthElement.BlockInformation> toRemove = new ArrayList<>();
        for (EarthElement.BlockInformation entry : blocksToRestore) {
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

    public static void addBlockToRestore(EarthElement.BlockInformation info){
        blocksToRestore.add(info);
    }
    
    public static void reset() {
        blocksToRestore.clear();
    }

}
