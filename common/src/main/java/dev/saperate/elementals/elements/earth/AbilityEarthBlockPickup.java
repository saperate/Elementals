package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AbilityEarthBlockPickup implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        if (!bender.reduceChi(10)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Object[] vars = EarthElement.canBend(player, true);
        if (vars != null) {
            Vec3 pos = (Vec3) vars[0];
            BlockState state = (BlockState) vars[1];

            EarthBlockEntity entity = new EarthBlockEntity(player.level(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            entity.setBlockState(state);

            player.level().addFreshEntity(entity);
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
        entity.setDeltaMovement(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, speed, 0);
        entity.setDamage(plrData.canUseUpgrade("earthBlockDamageI") ? 8 : 4);
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (started) {
            return;
        }
        Player player = bender.player;

        EarthBlockEntity blockEntity = (EarthBlockEntity) bender.abilityData;
        onRemove(bender);
        if (blockEntity == null || !PlayerData.get(player).canUseUpgrade("earthBlockShrapnel") || !player.isShiftKeyDown()) {
            return;
        }
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1.5f;
        if (plrData.canUseUpgrade("earthBlockSpeedII")) {
            speed = 2.5f;
        } else if (plrData.canUseUpgrade("earthBlockSpeedI")) {
            speed = 2;
        }
        blockEntity.setDeltaMovement(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, speed, 0);
        blockEntity.setModelShapeId(1);
        blockEntity.setDamage(plrData.canUseUpgrade("earthBlockDamageI") ? 12 : 8);
        blockEntity.setShiftToFreeze(false);
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
