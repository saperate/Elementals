package dev.saperate.elementals.items.scrolls;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FireScrollItem extends Item {

    public FireScrollItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!user.level().isClientSide) {
            Bender bender = Bender.getBender((ServerPlayer) user);
            if(bender.addElement(FireElement.get(), true)){
                user.getInventory().removeItem(user.getItemInHand(hand));
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.translatable("item.elementals.fire_scroll.tooltip"));
    }

}