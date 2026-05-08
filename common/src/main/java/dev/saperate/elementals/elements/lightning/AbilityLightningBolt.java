package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.lightning.LightningArcEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityLightningBolt implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;

        float cost = 35;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("lightningBoltEfficiencyII")) {
            cost = 25;
        } else if (plrData.canUseUpgrade("lightningBoltEfficiencyI")) {
            cost = 30;
        }
        if (!bender.reduceChi(cost)) {
            bender.setCurrAbility(null);
            return;
        }

        Vector3f pos = getEntityLookVector(player, 2).toVector3f();

        LightningArcEntity entity = new LightningArcEntity(player.level(), player, pos.x, pos.y, pos.z);
        entity.makeChild();

        bender.abilityData = entity;
        player.level().addFreshEntity(entity);


        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        LightningArcEntity e = (LightningArcEntity) bender.abilityData;
        if(e.getTail().chainLength == LightningArcEntity.MAX_CHAIN_LENGTH){
            Vec3 pos = SapsUtils.raycastFull(bender.player, 100,true).getLocation();
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT,e.level());
            lightning.setPos(pos.x,pos.y,pos.z);
            e.level().addFreshEntity(lightning);
            onRemove(bender);
        }
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {
        if(!bender.player.isCrouching()){
            onRemove(bender);
        }
        bender.player.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIONARY,20, 0, false, false, false));
    }

    @Override
    public void onRemove(Bender bender) {
        LightningArcEntity e = (LightningArcEntity) bender.abilityData;
        bender.setCurrAbility(null);
        if(e == null){
            return;
        }
        e.remove();
    }
}
