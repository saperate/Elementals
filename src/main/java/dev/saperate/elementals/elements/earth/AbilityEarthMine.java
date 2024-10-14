package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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

        if (!bender.reduceChi(1.5f * (deltaT > 500 ? dS * dS * dS : 1 ))) {
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
        System.out.println(dS);
        System.out.println(numBlocks);
        for (int i = 0; i < numBlocks; i++) {
            player.getWorld().breakBlock(pos.offset(dir,i),true);
        }

        bender.setCurrAbility(null);
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

    }
}
