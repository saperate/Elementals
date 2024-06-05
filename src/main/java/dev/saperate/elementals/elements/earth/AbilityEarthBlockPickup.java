package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
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

        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1;
        if (plrData.canUseUpgrade("earthBlockSpeedII")) {
            speed = 2;
        } else if (plrData.canUseUpgrade("earthBlockSpeedI")) {
            speed = 1.5f;
        }
        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 0);
        entity.setDamage(plrData.canUseUpgrade("earthBlockDamageI") ? 2.5f : 3);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if(started){
            return;
        }
        PlayerEntity player = bender.player;

        EarthBlockEntity blockEntity = (EarthBlockEntity) bender.abilityData;
        onRemove(bender);
        if(blockEntity == null || !PlayerData.get(player).canUseUpgrade("earthBlockShrapnel")){
            return;
        }
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1.5f;
        if (plrData.canUseUpgrade("earthBlockSpeedII")) {
            speed = 2.5f;
        } else if (plrData.canUseUpgrade("earthBlockSpeedI")) {
            speed = 2;
        }
        blockEntity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 0);
        blockEntity.setModelShapeId(1);
        blockEntity.setDamage(plrData.canUseUpgrade("earthBlockDamageI") ? 3 : 2.5f);
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
