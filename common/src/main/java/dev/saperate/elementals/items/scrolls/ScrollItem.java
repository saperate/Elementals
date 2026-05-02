package dev.saperate.elementals.items.scrolls;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ScrollItem extends Item {

    public ScrollItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return super.use(world, user, hand);
    }
}