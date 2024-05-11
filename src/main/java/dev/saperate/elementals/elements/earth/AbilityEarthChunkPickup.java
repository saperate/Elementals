package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import java.util.LinkedList;

public class AbilityEarthChunkPickup implements Ability {
    //TODO make an ability that makes it so you can throw the chunk one block at a time
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        Object[] vars = EarthElement.canBend(player, false);
        if (vars == null) {
            bender.setCurrAbility(null);
            return;
        }

        LinkedList<EarthBlockEntity> entities = new LinkedList<>();
        BlockPos pos = (BlockPos) vars[2];

        int size = (plrData.canUseUpgrade("biggerChunk") ? 2 : 1);//TODO make this toggleable when you bought the upgrade

        for (int y = 0; y < size + size; y++) {
            for (int x = -size; x < size; x++) {
                for (int z = -size; z < size; z++) {
                    BlockPos bPos = pos.add(x,-y,z);
                    BlockState state = player.getWorld().getBlockState(bPos);
                    if(EarthElement.isBlockBendable(state)){
                        player.getWorld().setBlockState(bPos, Blocks.AIR.getDefaultState());

                        EarthBlockEntity entity = new EarthBlockEntity(player.getWorld(), player, bPos.getX() + 0.5f, bPos.getY(), bPos.getZ() + 0.5f);
                        entity.setBlockState(state);
                        entity.setTargetPosition(new Vector3f(x,y - (size / 2),z));
                        entity.setUseOffset(true);

                        player.getWorld().spawnEntity(entity);
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
            entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 1, 0);
            entity.setControlled(false);
            entity.setCollidable(false);
        }
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

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
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }
}
