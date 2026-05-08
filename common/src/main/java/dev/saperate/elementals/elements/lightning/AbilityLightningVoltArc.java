package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.lightning.VoltArcEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityLightningVoltArc implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        if (!bender.reduceChi(10)) {
            bender.setCurrAbility(null);
            return;
        }

        Vector3f pos = getEntityLookVector(player, 2.5f).toVector3f();

        VoltArcEntity entity = new VoltArcEntity(player.level(), player, pos.x, pos.y, pos.z);
        entity.makeChild();

        int duration = 200;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("lightningVoltArcStrengthII")) {
            duration = 600;
        } else if (plrData.canUseUpgrade("lightningVoltArcStrengthI")) {
            duration = 400;
        }

        entity.duration = duration;

        bender.abilityData = entity;
        player.level().addFreshEntity(entity);
        entity.setDeltaMovement(0.001f, 0.001f, 0.001f);


        bender.setCurrAbility(this);
    }


    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {
        VoltArcEntity entity = (VoltArcEntity) bender.abilityData;
        if (entity != null && entity.tickCount >= 5){
            entity.setDeltaMovement(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, 4, 0);
            entity.setControlled(false);
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        VoltArcEntity entity = (VoltArcEntity) bender.abilityData;
        if(entity != null){
            entity.discard();
        }
    }
}
