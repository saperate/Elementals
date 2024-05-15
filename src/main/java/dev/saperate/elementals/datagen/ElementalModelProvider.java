package dev.saperate.elementals.datagen;

import dev.saperate.elementals.items.ElementalItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.ArmorItem;

public class ElementalModelProvider extends FabricModelProvider {
    public ElementalModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.registerArmor(((ArmorItem) ElementalItems.EARTH_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ElementalItems.EARTH_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ElementalItems.EARTH_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ElementalItems.EARTH_BOOTS));
    }
}
