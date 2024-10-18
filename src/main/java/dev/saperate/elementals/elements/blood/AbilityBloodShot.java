package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.blood.BloodShotEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityBloodShot implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        float cost = 20;
        if (bender.getData().canUseUpgrade("bloodShotEfficiencyII")) {
            cost = 10;
        } else if (bender.getData().canUseUpgrade("bloodShotEfficiencyI")) {
            cost = 15;
        }

        if (!bender.reduceChi(cost)) {
            bender.setCurrAbility(null);
            return;
        }

        Vector3f pos = getEntityLookVector(player, 2.5f).toVector3f();


        BloodShotEntity entity = new BloodShotEntity(
                player.getWorld(),
                player,
                pos.x, pos.y, pos.z,
                player.getActiveStatusEffects()
        );
        entity.effects.addAll(SapsUtils.getEffectsFromHands(player));


        bender.abilityData = entity;
        player.getWorld().spawnEntity(entity);
        entity.setVelocity(0.001f, 0.001f, 0.001f);

        player.damage(player.getDamageSources().dryOut(),2);

        bender.setCurrAbility(this);
    }


    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {
        BloodShotEntity entity = (BloodShotEntity) bender.abilityData;
        if (entity != null && entity.age >= 5) {
            entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 4, 0);
            entity.setControlled(false);
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        BloodShotEntity entity = (BloodShotEntity) bender.abilityData;
        if (entity != null) {
            entity.discard();
        }
    }
}
