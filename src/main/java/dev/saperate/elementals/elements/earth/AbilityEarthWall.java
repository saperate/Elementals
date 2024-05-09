package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;

public class AbilityEarthWall implements Ability {
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

        double dx = -Math.sin(Math.toRadians(player.getYaw() - 90));
        double dz = Math.cos(Math.toRadians(player.getYaw() - 90));

        for (int i = 1; i <= (plrData.canUseUpgrade("widerWall") ? 4 : 2); i++) {
            int dxScaled = (int) Math.round(dx * i);
            int dzScaled = (int) Math.round(dz * i);

            placePillar(pos.add(dxScaled,0,dzScaled),3, entities, bender);
            placePillar(pos.add(-dxScaled,0,-dzScaled),3, entities, bender);
        }
        placePillar(pos,3, entities, bender);
        bender.setCurrAbility(this);
    }

    public static void placePillar(BlockPos startPos, int height, LinkedList<EarthBlockEntity> entities, Bender bender){
        PlayerEntity player = bender.player;

        for (int y = 0; y < height; y++) {
            BlockPos bPos = new BlockPos(
                    startPos.getX(),
                    (startPos.getY() - y),
                    startPos.getZ());
            BlockState state = player.getWorld().getBlockState(bPos);

            if(!EarthElement.isBlockBendable(state)){
                return;
            }
            player.getWorld().setBlockState(bPos, Blocks.AIR.getDefaultState());

            EarthBlockEntity entity = new EarthBlockEntity(player.getWorld(), player, startPos.getX(), startPos.getY() - y, startPos.getZ());
            bender.abilityData = entity;
            entity.setBlockState(state);
            entity.setTargetPosition(startPos.add(0,height - y,0).toCenterPos().toVector3f());

            player.getWorld().spawnEntity(entity);
        }
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        EarthBlockEntity entity = (EarthBlockEntity) bender.abilityData;
        onRemove(bender);
        if (entity == null) {
            return;
        }

        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 1, 0);

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (started) {
            return;
        }
        PlayerEntity player = bender.player;

        EarthBlockEntity blockEntity = (EarthBlockEntity) bender.abilityData;
        onRemove(bender);
        if (blockEntity == null || !PlayerData.get(player).canUseUpgrade("shrapnel")) {
            return;
        }
        blockEntity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 1, 0);
        blockEntity.setIsShrapnel(true);
    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        EarthBlockEntity entity = (EarthBlockEntity) bender.abilityData;
        bender.abilityData = null;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
    }
}
