package dev.saperate.elementals.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

import static dev.saperate.elementals.Elementals.MODID;

public class SoulFireCore extends Block {


    public SoulFireCore(Settings settings) {
        super(settings);
    }

    public static Block registerBlock(){
        return Registry.register(Registries.BLOCK,new Identifier(MODID,"soulfirecore"), new SoulFireCore(FabricBlockSettings.create().strength(1f)));
    }
}
