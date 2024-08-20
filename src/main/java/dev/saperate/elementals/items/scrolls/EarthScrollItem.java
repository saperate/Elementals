package dev.saperate.elementals.items.scrolls;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.earth.EarthElement;
import dev.saperate.elementals.elements.fire.FireElement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class EarthScrollItem extends Item {

    public EarthScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient) {
            Bender bender = Bender.getBender(user);
            bender.addElement(EarthElement.get(), true);
        }
        return super.use(world, user, hand);
    }

}