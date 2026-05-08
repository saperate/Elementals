package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

//TODO add compat with blue fire overlay mod
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
                },2),
                new Upgrade("fireWisp",4)
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
        addAbility(new AbilityFire4(), true);
        addAbility(new AbilityFireWisp());
    }

    public static void placeFire(BlockPos pos, Direction side, Entity entity, BlockState state){
        BlockPos newPos = pos.relative(side);

        entity.level().playSound(entity, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, entity.level().getRandom().nextFloat() * 0.4F + 0.8F);

        if(state.getProperties().contains(BlockStateProperties.LIT)){
            entity.level().setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
            entity.level().gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
        }else{
            entity.level().setBlockAndUpdate(newPos, BaseFireBlock.getState(entity.level(),newPos));
        }
    }

    public static Element get(){
        return getElement("Fire");
    }

    @Override
    public int getColor() {
        return 0xFFFD9A4D;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFFB32616;
    }


    @Override
    public int getTertiaryColor() {
        return 0xFF7a0845;
    }

    @Override
    public String[] getBackgroundTextures() {
        return new String[]{"bottom.png"};
    }
    

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        PlayerData plrData = bender.plrData;
        return bender.hasElement(this)
                && (plrData.canUseUpgrade("blueFire") || plrData.canUseUpgrade("fireWallWideI")|| plrData.canUseUpgrade("fireWallTallI") || (plrData.canUseUpgrade("fireSpikesCountI") && plrData.canUseUpgrade("fireSpikesRangeI")))
                && plrData.canUseUpgrade("fireBallSpeedII")
                && (plrData.canUseUpgrade("flameThrower") || plrData.canUseUpgrade("fireShield"))
                && plrData.canUseUpgrade("fireArcMastery")
                && plrData.canUseUpgrade("fireJetSpeedII")
                && plrData.canUseUpgrade("fireJumpRangeII")
                ;
    }
}
