package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;

public class AbilityEarthMine implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        //TODO make it so it consumes more chi if the block is harder to break

        Object[] vars = EarthElement.canBend(player, false);
        if (vars == null || !player.getWorld().getGameRules().getBoolean(BENDING_GRIEFING)) {
            bender.setCurrAbility(null);
            return;
        }
        float dS = Math.min(4, (float) deltaT / 1000);

        if (!bender.reduceChi(1.5f * (deltaT > 500 ? dS * dS : 1 ))) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        BlockPos pos = (BlockPos) vars[2];
        Direction dir = ((Direction) vars[3]).getOpposite();

        int numBlocks = (int) (Math.floor(dS * 2)) + 1;

        if(dS == 4){
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if((i == j || -i == j) && i != 0){
                        continue;
                    }
                    BlockPos offset = switch (dir) {
                        case SOUTH, NORTH ->
                                pos.add(i, j, 0);
                        case WEST, EAST ->
                                pos.add(0, i, j);
                        default -> pos.add(i, 0, j);
                    };
                    minePillar(offset, dir,numBlocks,player.getWorld());
                }
            }
        }else {
            minePillar(pos,dir,numBlocks,player.getWorld());
        }

        bender.setCurrAbility(null);
    }

    public void minePillar(BlockPos pos, Direction dir, int amount, World world){
        for (int i = 0; i < amount; i++) {
            if(EarthElement.isBlockBendable(pos.offset(dir,i),world)){
                world.breakBlock(pos.offset(dir,i),true);
            }
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }
}
