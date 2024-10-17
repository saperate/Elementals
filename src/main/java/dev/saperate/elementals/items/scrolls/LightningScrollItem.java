package dev.saperate.elementals.items.scrolls;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.elements.lightning.LightningElement;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;


public class LightningScrollItem extends Item {

    public LightningScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!user.getWorld().isClient){
            Bender bender = Bender.getBender((ServerPlayerEntity) user);
            if(!bender.hasElement(LightningElement.get()) && FireElement.get().isSkillTreeComplete(bender)){
                if(bender.addElement(LightningElement.get(), true)){
                    user.getInventory().removeOne(user.getStackInHand(hand));
                }
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.elementals.lightning_scroll.tooltip"));
    }

}