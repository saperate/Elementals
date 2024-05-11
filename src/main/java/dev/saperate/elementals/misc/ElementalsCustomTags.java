package dev.saperate.elementals.misc;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ElementalsCustomTags {
    public static final TagKey<Block> EARTH_BENDABLE_BLOCKS = createTag("earth_bendable_blocks");

    private static TagKey<Block> createTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(MODID, name));
    }
}
