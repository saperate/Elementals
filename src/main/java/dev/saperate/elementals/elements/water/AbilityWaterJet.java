package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterJet implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
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
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (started) {
            PlayerEntity player = bender.player;

            Vec3d pos = getEntityLookVector(player, .5f);

            WaterJetEntity parent = new WaterJetEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.abilityData = parent;
            player.getWorld().spawnEntity(parent);

            WaterJetEntity child = new WaterJetEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            parent.setChild(child);
            player.getWorld().spawnEntity(child);

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
            PlayerEntity player = bender.player;
            serverSummonParticles((ServerWorld) player.getWorld(),
                    ParticleTypes.SPLASH, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, 0, 0, 0);
        } else {
            bender.player.addStatusEffect(new StatusEffectInstance(STATIONARY_EFFECT, 1, 1, false, false, false));
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
