package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.elements.Element;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;


public class FireElement extends Element {
    public FireElement() {
        super("Fire");
        addAbility(new AbilityFireIgnite());
    }

    public static void placeFire(BlockPos pos, Direction side, PlayerEntity player, BlockState state){
        BlockPos newPos = pos.offset(side);

        player.getWorld().playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, player.getWorld().getRandom().nextFloat() * 0.4F + 0.8F);
        if(state.getProperties().contains(Properties.LIT)){
            player.getWorld().setBlockState(pos, state.with(Properties.LIT, true), 11);
            player.getWorld().emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }else{
            player.getWorld().setBlockState(newPos, AbstractFireBlock.getState(player.getWorld(),newPos));
        }
    }
}
