package dev.saperate.elementals.misc;


import dev.saperate.elementals.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ElementalsCustomTags {
    public static final TagKey<Block> EARTH_BENDABLE_BLOCKS = createTag("earth_bendable_blocks");
    public static final TagKey<Block> METAL_BENDABLE_BLOCKS = createTag("metal_bendable_blocks");

    private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MODID, name));
    }
}
