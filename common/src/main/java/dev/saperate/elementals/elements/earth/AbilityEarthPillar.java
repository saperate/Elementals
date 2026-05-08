package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;

public class AbilityEarthPillar implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {

        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        Object[] vars = EarthElement.canBend(player, false);
        if (vars == null) {
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        BlockPos startPos = (BlockPos) vars[2];
        //we want to get into the ground instead of above, so we get the opposite
        Direction dir = ((Direction) vars[3]).getOpposite();



        int height = (plrData.canUseUpgrade("earthPillarTallI") ? 5 : 3);
        for (int i = 0; i < height; i++) {
            if (dir.equals(Direction.UP)){
                BlockPos bPos = startPos.relative(dir,i);
                BlockState state = player.level().getBlockState(bPos);

                if(!EarthElement.isBlockBendable(state, bender)){
                    return;
                }
                FallingBlockEntity.fall(player.level(),bPos,state);
                continue;
            }
            BlockPos pos = startPos.relative(dir,i);
            Vec3 target = startPos.getCenter().relative(dir.getOpposite(), height - i + 0.1f);
            placeBlock(pos, target, player);
        }


        bender.setCurrAbility(null);
    }

    public static void placeBlock(BlockPos startPos, Vec3 endPos, Player player){
            BlockState state = player.level().getBlockState(startPos);

            if(!EarthElement.isBlockBendable(state, Bender.getBender((ServerPlayer) player))){
                return;
            }
            if(player.level().getGameRules().getBoolean(BENDING_GRIEFING)){
                player.level().setBlockAndUpdate(startPos, Blocks.AIR.defaultBlockState());
            }


            EarthBlockEntity entity = new EarthBlockEntity(player.level(), player, startPos.getX() + 0.5f, startPos.getY(), startPos.getZ() + 0.5f);
            entity.setBlockState(state);
            entity.setTargetPosition(endPos.toVector3f());
            entity.setShiftToFreeze(false);
            entity.setDamageOnTouch(true);//TODO replace with damage above block
            entity.setDamage(1);
            entity.maxLifeTime = 20;
            entity.setDropOnEndOfLife(true);
            entity.setMovementSpeed(0.5f);

            player.level().addFreshEntity(entity);
    }
    
    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }
}
