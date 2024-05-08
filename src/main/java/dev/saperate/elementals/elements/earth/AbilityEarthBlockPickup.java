package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.entities.earth.EarthShrapnelEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AbilityEarthBlockPickup implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        Object[] vars = EarthElement.canBend(player, true);

        if (vars != null) {
            Vec3d pos = (Vec3d) vars[0];
            BlockState state = (BlockState) vars[1];

            EarthBlockEntity entity = new EarthBlockEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            entity.setBlockState(state);

            player.getWorld().spawnEntity(entity);
            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
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
        PlayerEntity player = bender.player;

        EarthBlockEntity blockEntity = (EarthBlockEntity) bender.abilityData;
        onRemove(bender);
        if (blockEntity == null) {
            return;
        }
        if(!PlayerData.get(player).canUseUpgrade("shrapnel")){
            return;
        }
        EarthShrapnelEntity shrapnel = new EarthShrapnelEntity(player.getWorld(), player, blockEntity.getX(),blockEntity.getY(),blockEntity.getZ());
        shrapnel.setBlockState(blockEntity.getBlockState());
        shrapnel.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 1, 0);
        shrapnel.setControlled(false);

        player.getWorld().spawnEntity(shrapnel);
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
