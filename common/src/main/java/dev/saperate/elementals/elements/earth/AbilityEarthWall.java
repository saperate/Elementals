package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.misc.BlockRestoreManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;

public class AbilityEarthWall implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        if (!plrData.canUseUpgrade("earthWall")) {
            bender.setCurrAbility(null);
            return;
        }

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

        LinkedList<EarthBlockEntity> entities = new LinkedList<>();
        BlockPos pos = (BlockPos) vars[2];

        double dx = -Math.sin(Math.toRadians(player.getYRot() - 90));
        double dz = Math.cos(Math.toRadians(player.getYRot() - 90));

        for (int i = 1; i <= (plrData.canUseUpgrade("widerWall") ? 4 : 2); i++) {
            int dxScaled = (int) Math.round(dx * i);
            int dzScaled = (int) Math.round(dz * i);

            placePillar(pos.offset(dxScaled,0,dzScaled),3, entities, bender);
            placePillar(pos.offset(-dxScaled,0,-dzScaled),3, entities, bender);
        }
        placePillar(pos,3, entities, bender);

        bender.abilityData = entities;
        if(plrData.canUseUpgrade("earthWallAutoTimer")){
            onRightClick(bender, false);
        }else{
            bender.setCurrAbility(this);
        }
    }

    public static void placePillar(BlockPos startPos, int height, LinkedList<EarthBlockEntity> entities, Bender bender){
        Player player = bender.player;

        for (int y = 0; y < height; y++) {
            BlockPos bPos = new BlockPos(
                    startPos.getX(),
                    (startPos.getY() - y),
                    startPos.getZ());
            BlockState state = player.level().getBlockState(bPos);

            if(!EarthElement.isBlockBendable(state, bender)){
                return;
            }

            player.level().setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState());
            if(!player.level().getGameRules().getBoolean(BENDING_GRIEFING)){
                BlockRestoreManager.addBlockToRestore(new BlockRestoreManager.BlockInformation(
                        bPos,
                        state,
                        player.level().dimension(),
                        40 + player.level().random.nextInt(0, 140)
                ));
            }


            EarthBlockEntity entity = new EarthBlockEntity(player.level(), player, startPos.getX() + 0.5f, startPos.getY() - y, startPos.getZ() + 0.5f);
            entity.setBlockState(state);
            entity.setTargetPosition(startPos.offset(0,height - y,0).getCenter().toVector3f().add(0,0.05f,0));
            entity.setMovementSpeed(0.2f);
            player.level().addFreshEntity(entity);
            entities.add(entity);
        }
    }
    

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (started) {
            return;
        }
        LinkedList<EarthBlockEntity> entities = (LinkedList<EarthBlockEntity>) bender.abilityData;
        onRemove(bender);
        if(entities == null){
            return;
        }
        PlayerData plrData = PlayerData.get(bender.player);
        boolean canUseTimer = plrData.canUseUpgrade("earthWallDurationI");

        int timer = 100;
        if(canUseTimer){
            if (plrData.canUseUpgrade("earthWallDurationIV")) {
                timer = 1200;
            } else if (plrData.canUseUpgrade("earthWallDurationIII")) {
                timer = 600;
            } else if (plrData.canUseUpgrade("earthWallDurationII")) {
                timer = 300;
            }
        }

        for (EarthBlockEntity entity : entities){
            if((!canUseTimer && bender.player.isShiftKeyDown()) || !canUseTimer){
                entity.setControlled(false);
            }else {
                entity.setShiftToFreeze(false);
                entity.setDropOnEndOfLife(true);
                entity.maxLifeTime = timer;
            }
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }
}
