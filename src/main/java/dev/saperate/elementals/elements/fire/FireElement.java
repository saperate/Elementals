package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;


public class FireElement extends Element {
    public FireElement() {
        super("Fire", new Upgrade[]{
                new Upgrade("flareUp",new Upgrade[]{
                        new Upgrade("blueFire"),
                        new Upgrade("fireWall", new Upgrade[]{
                                new Upgrade("widerWall"),
                                new Upgrade("tallerWall")
                        }, true),
                        new Upgrade("fireSpikes")
                }, true)
        });
        addAbility(new AbilityFire1(),true);
        addAbility(new AbilityFireIgnite(), false);
        addAbility(new AbilityFireWall(), false);
        addAbility(new AbilityFireSpikes(), false);
        addAbility(new AbilityFire2(),true);
        addAbility(new AbilityFireArc(),false);
        addAbility(new AbilityFireBall(),false);
    }

    public static void placeFire(BlockPos pos, Direction side, Entity entity, BlockState state){
        BlockPos newPos = pos.offset(side);

        entity.getWorld().playSound(entity, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, entity.getWorld().getRandom().nextFloat() * 0.4F + 0.8F);

        System.out.println(state.getProperties());
        if(state.getProperties().contains(Properties.LIT)){
            entity.getWorld().setBlockState(pos, state.with(Properties.LIT, true), 11);
            entity.getWorld().emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
        }else{
            entity.getWorld().setBlockState(newPos, AbstractFireBlock.getState(entity.getWorld(),newPos));
        }
    }

    public static Element get(){
        return elementList.get(2);
    }
}
