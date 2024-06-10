package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;

public class AbilityEarthPillar implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {

        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        Object[] vars = EarthElement.canBend(player, false);
        if (vars == null) {
            bender.setCurrAbility(null);
            return;
        }
        bender.reduceChi(15);
        BlockPos startPos = (BlockPos) vars[2];
        //we want to get into the ground instead of above, so we get the opposite
        Direction dir = ((Direction) vars[3]).getOpposite();



        int height = (plrData.canUseUpgrade("earthPillarTallI") ? 5 : 3);
        for (int i = 0; i < height; i++) {
            if (dir.equals(Direction.UP)){
                BlockPos bPos = startPos.offset(dir,i);
                BlockState state = player.getWorld().getBlockState(bPos);

                if(!EarthElement.isBlockBendable(state)){
                    return;
                }
                FallingBlockEntity.spawnFromBlock(player.getWorld(),bPos,state);
                continue;
            }
            BlockPos pos = startPos.offset(dir,i);
            Vec3d target = startPos.toCenterPos().offset(dir.getOpposite(), height - i + 0.1f);
            placeBlock(pos, target, player);
        }


        bender.setCurrAbility(null);
    }

    public static void placeBlock(BlockPos startPos, Vec3d endPos, PlayerEntity player){
            BlockState state = player.getWorld().getBlockState(startPos);

            if(!EarthElement.isBlockBendable(state)){
                return;
            }
            player.getWorld().setBlockState(startPos, Blocks.AIR.getDefaultState());

            EarthBlockEntity entity = new EarthBlockEntity(player.getWorld(), player, startPos.getX() + 0.5f, startPos.getY(), startPos.getZ() + 0.5f);
            entity.setBlockState(state);
            entity.setTargetPosition(endPos.toVector3f());
            entity.setShiftToFreeze(false);
            entity.setDamageOnTouch(true);
            entity.setLifeTime(20);
            entity.setDropOnEndOfLife(true);
            entity.setMovementSpeed(0.3f);

            player.getWorld().spawnEntity(entity);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }
}
