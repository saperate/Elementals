package dev.saperate.elementals.platform;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.platform.services.IItemHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static dev.saperate.elementals.items.ElementalItems.*;

public class FabricItemHelper implements IItemHelper {

    @Override
    public CreativeModeTab createItemTab() {
        return FabricItemGroup.builder()
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
}
