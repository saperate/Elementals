package dev.saperate.elementals.items.scrolls;


import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.metal.MetalElement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class AbstractScrollItem extends Item {

    public AbstractScrollItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player user, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            Bender bender = Bender.getBender((ServerPlayer) user);
            if(!bender.hasElement(getElement()) 
                    && (getParentElement() != null && getParentElement().isSkillTreeComplete(bender))){
                if(bender.addElement(MetalElement.get(), true)){
                    user.getInventory().removeItem(user.getItemInHand(hand));
                }
            }
        }
        return super.use(level, user, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.translatable(getTranslatable()));
    }
    
    abstract String getTranslatable();
    
    abstract Element getElement();
    
    Element getParentElement(){
        return null;
    };

}