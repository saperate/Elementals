package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterJet implements Ability {
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
        Vector3f pos = WaterElement.canBend(player, true);

        if (pos != null) {
            bender.abilityData = null;
            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
    }


    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (started) {
            Player player = bender.player;

            Vec3 pos = getEntityLookVector(player, .5f);

            WaterJetEntity parent = new WaterJetEntity(player.level(), player, pos.x, pos.y, pos.z);
            bender.abilityData = parent;
            player.level().addFreshEntity(parent);

            WaterJetEntity child = new WaterJetEntity(player.level(), player, pos.x, pos.y, pos.z);
            parent.setChild(child);
            player.level().addFreshEntity(child);

            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("waterJetRangeI")) {
                parent.setRange(15);
                child.setRange(15);
            }
            if (plrData.canUseUpgrade("waterJetDamageI")) {
                parent.setStreamSize(2);
                child.setStreamSize(2);
            }

        } else {
            onRemove(bender);
        }
    }

    @Override
    public void onTick(Bender bender) {
        if (bender.abilityData == null) {
            Player player = bender.player;
            serverSummonParticles((ServerLevel) player.level(),
                    ParticleTypes.SPLASH, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, 0, 0, 0);
        } else {
            bender.player.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIONARY, 1, 1, false, false, false));
            if (!bender.reduceChi(0.2f)) {
                if (bender.abilityData == null) {
                    bender.setCurrAbility(null);
                } else {
                    onRemove(bender);
                }
                return;
            }
        }
    }

    @Override
    public void onRemove(Bender bender) {
        WaterJetEntity entity = (WaterJetEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.discard();
        entity.getChild().discard();
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}
