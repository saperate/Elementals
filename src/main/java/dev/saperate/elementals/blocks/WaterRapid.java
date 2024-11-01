package dev.saperate.elementals.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class WaterRapid extends Block {


    public WaterRapid(Settings settings) {
        super(settings);
    }

    public static Block registerBlock(){
        return Registry.register(Registries.BLOCK,Identifier.of(MODID,"waterrapid"), new WaterRapid(FabricBlockSettings.create().strength(1f)));
    }
}
