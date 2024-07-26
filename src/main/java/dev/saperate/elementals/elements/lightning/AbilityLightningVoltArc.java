package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.lightning.LightningArcEntity;
import dev.saperate.elementals.entities.lightning.VoltArcEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityLightningVoltArc implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        Vector3f pos = getEntityLookVector(player, 1).toVector3f();

        VoltArcEntity entity = new VoltArcEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        entity.makeChild();

        bender.abilityData = entity;
        player.getWorld().spawnEntity(entity);
        entity.setVelocity(0.001f, 0.001f, 0.001f);


        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {
        VoltArcEntity entity = (VoltArcEntity) bender.abilityData;
        if (entity != null && entity.age % 2 == 0){
            entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 4, 0);
            entity.setControlled(false);
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onRemove(Bender bender) {
    }
}
