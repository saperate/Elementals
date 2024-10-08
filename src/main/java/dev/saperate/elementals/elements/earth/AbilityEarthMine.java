package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

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

        if (!bender.reduceChi(2.5f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        BlockPos pos = (BlockPos) vars[2];
        player.getWorld().breakBlock(pos,true);
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
