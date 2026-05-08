package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityFireBall implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(30)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;

        Vector3f pos = getEntityLookVector(player, 2).toVector3f();

        FireBallEntity entity = new FireBallEntity(player.level(), player, pos.x, pos.y, pos.z);
        bender.abilityData = entity;
        entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
        player.level().addFreshEntity(entity);

        bender.setCurrAbility(this);

    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        FireBallEntity entity = (FireBallEntity) bender.abilityData;
        if(entity == null){
            throw new RuntimeException("Elementals: Tried to launch entity while having none!");
        }
        onRemove(bender);

        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1;
        if (plrData.canUseUpgrade("fireArcSpeedII")) {
            speed = 2;
        } else if (plrData.canUseUpgrade("fireArcSpeedI")) {
            speed = 1.5f;
        }
        entity.setDeltaMovement(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, speed, 0);
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }
    
    @Override
    public void onRemove(Bender bender) {
        FireBallEntity entity = (FireBallEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        bender.setCurrAbility(null);
    }
}
