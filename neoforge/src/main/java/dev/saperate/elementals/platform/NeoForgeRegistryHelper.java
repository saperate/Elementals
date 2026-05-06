package dev.saperate.elementals.platform;

import dev.saperate.elementals.platform.services.IRegistryHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static dev.saperate.elementals.items.ElementalItems.*;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    @Override
    public CreativeModeTab createItemTab() {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(SCROLL_ITEM))
                .title(Component.literal("Elementals"))
                .displayItems((context,entries) -> {
                    entries.accept(SCROLL_ITEM);
                    entries.accept(FIRE_SCROLL_ITEM);
                    entries.accept(WATER_SCROLL_ITEM);
                    entries.accept(EARTH_SCROLL_ITEM);
                    entries.accept(AIR_SCROLL_ITEM);
                    entries.accept(LIGHTNING_SCROLL_ITEM);
                    entries.accept(BLOOD_SCROLL_ITEM);
                    entries.accept(METAL_SCROLL_ITEM);
                    entries.accept(DIRT_BOTTLE_ITEM);
                    entries.accept(LIGHTNING_BOTTLE_ITEM);
                    entries.accept(BOOMERANG_ITEM);
                    entries.accept(WATER_POUCH_ITEM);
                    entries.accept(GLIDER_ITEM);
                }).build();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityTypeFactory<T> factory, Block... validBlocks) {
        return BlockEntityType.Builder.of(factory::create, validBlocks).build(null);
    }
}
