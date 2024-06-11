package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
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
                new Upgrade("fireIgnition", new Upgrade[]{
                        new Upgrade("fireFlareUp",new Upgrade[]{
                                new Upgrade("blueFire",2),
                                new Upgrade("fireWall", new Upgrade[]{
                                        new Upgrade("fireWallWideI",1),
                                        new Upgrade("fireWallTallI",1)
                                }, true,2),
                                new Upgrade("fireSpikes", new Upgrade[]{
                                        new Upgrade("fireSpikesCountI",1),
                                        new Upgrade("fireSpikesRangeI",1)
                                },false,1,2)
                        }, true,1),
                },2),
                new Upgrade("fireArc", new Upgrade[]{
                        new Upgrade("fireBall", new Upgrade[]{
                                new Upgrade("fireBallSpeedI", new Upgrade[]{
                                        new Upgrade("fireBallSpeedII",1)
                                },1)
                        }, false, -1,2),
                        new Upgrade("fireArcDamageI", new Upgrade[]{
                                new Upgrade("flameThrower",2),
                                new Upgrade("fireShield", 2)
                        }, true, 1),
                        new Upgrade("fireArcEfficiencyI", new Upgrade[]{
                                new Upgrade("fireArcSpeedI", new Upgrade[]{
                                        new Upgrade("fireArcSpeedII", new Upgrade[]{
                                                new Upgrade("fireArcMastery",2)
                                        },1)
                                },1)
                        }, false, 1,1)
                },2),
                new Upgrade("fireJump", new Upgrade[]{
                        new Upgrade("fireJumpRangeI", new Upgrade[]{
                                new Upgrade("fireJumpRangeII",1),
                                new Upgrade("fireJet", new Upgrade[]{
                                        new Upgrade("fireJetSpeedI", new Upgrade[]{
                                                new Upgrade("fireJetSpeedII", 1)
                                        }, 1)
                                },2)
                        },1)
                },2)
        });
        addAbility(new AbilityFire1(),true);
        addAbility(new AbilityFireIgnite());
        addAbility(new AbilityFireWall());
        addAbility(new AbilityFireSpikes());
        addAbility(new AbilityFire2(), true);
        addAbility(new AbilityFireArc());
        addAbility(new AbilityFireBall());
        addAbility(new AbilityFireShield());
        addAbility(new AbilityFlameThrower());
        addAbility(new AbilityFire3(), true);
    }

    public static void placeFire(BlockPos pos, Direction side, Entity entity, BlockState state){
        BlockPos newPos = pos.offset(side);

        entity.getWorld().playSound(entity, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, entity.getWorld().getRandom().nextFloat() * 0.4F + 0.8F);

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
