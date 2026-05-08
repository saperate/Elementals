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
import org.joml.Vector3f;

import java.util.LinkedList;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;

public class AbilityEarthChunkPickup implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        Object[] vars = EarthElement.canBend(player, false);
        if (vars == null) {
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(30)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        LinkedList<EarthBlockEntity> entities = new LinkedList<>();
        BlockPos pos = (BlockPos) vars[2];

        float size = (plrData.canUseUpgrade("earthChunkSizeI") ? 1.5f : 1);

        for (int y = 0; y < size + size; y++) {
            for (int x = (int) -Math.floor(size); x < Math.ceil(size); x++) {
                for (int z = (int) -Math.floor(size); z < size; z++) {
                    BlockPos bPos = pos.offset(x,-y,z);
                    BlockState state = player.level().getBlockState(bPos);
                    if(EarthElement.isBlockBendable(state, bender)){
                        player.level().setBlockAndUpdate(bPos, Blocks.AIR.defaultBlockState());
                        if(!player.level().getGameRules().getBoolean(BENDING_GRIEFING)){
                            BlockRestoreManager.addBlockToRestore(new BlockRestoreManager.BlockInformation(
                                    bPos,
                                    state,
                                    player.level().dimension(),
                                    40 + player.level().random.nextInt(0, 140)
                            ));
                        }

                        EarthBlockEntity entity = new EarthBlockEntity(player.level(), player, bPos.getX() + 0.5f, bPos.getY(), bPos.getZ() + 0.5f);
                        entity.setBlockState(state);
                        entity.setTargetPosition(new Vector3f(x,y - (size / 2),z));
                        entity.setUseOffset(true);
                        entity.setDamage(6);

                        player.level().addFreshEntity(entity);
                        entities.add(entity);
                    }
                }
            }
        }

        bender.setCurrAbility(this);
        bender.abilityData = entities;
    }


    @Override
    public void onLeftClick(Bender bender, boolean started) {
        if (started) {
            return;
        }
        LinkedList<EarthBlockEntity> entities = (LinkedList<EarthBlockEntity>) bender.abilityData;
        onRemove(bender);
        if(entities == null){
            return;
        }

        for (EarthBlockEntity entity : entities){
            entity.setDeltaMovement(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, 1, 0);
            entity.setControlled(false);
            entity.setCollidable(false);
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

        for (EarthBlockEntity entity : entities){
            entity.setControlled(false);
        }
    }


    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }
}
