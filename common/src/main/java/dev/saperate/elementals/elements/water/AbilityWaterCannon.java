package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import dev.saperate.elementals.utils.MathHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterCannon implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(20)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Player player = bender.player;
        Vector3f pos = WaterElement.canBend(player, true);

        if (pos != null) {
            bender.abilityData = new Object[]{60, null}; //null is a placeholder, an entity will be there
            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onTick(Bender bender) {
        int chargeTime = (int) ((Object[]) bender.abilityData)[0];
        ((Object[]) bender.abilityData)[0] = chargeTime - 1;

        if (chargeTime == 0) {//Just got done charging
            Player player = bender.player;
            Vec3 pos = getEntityLookVector(player, .5f);

            WaterJetEntity parent = new WaterJetEntity(player.level(), player, pos.x, pos.y, pos.z);
            ((Object[]) bender.abilityData)[1] = parent;
            parent.setStreamSize(4);
            parent.setRange(20);
            player.level().addFreshEntity(parent);

            WaterJetEntity child = new WaterJetEntity(player.level(), player, pos.x, pos.y, pos.z);
            parent.setChild(child);
            parent.setStreamSize(4);
            parent.setRange(20);
            player.level().addFreshEntity(child);

            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("waterCannonRangeI")) {
                parent.setRange(15);
                child.setRange(15);
            }
            if (plrData.canUseUpgrade("waterCannonDamageI")) {
                parent.setStreamSize(6);
                child.setStreamSize(6);
            }

        } else if (chargeTime == -60) {//Reached end of life
            onRemove(bender);
        } else if (chargeTime > 0) {//charging
            Player player = bender.player;
            serverSummonParticles((ServerLevel) player.level(),
                    ParticleTypes.SPLASH, player, player.getRandom(),
                    0, 0.25f, 0,
                    0.1f, 2,
                    0, 0, 0, 0);
        } else {//Water cannon lifetime
            WaterJetEntity entity = (WaterJetEntity) ((Object[]) bender.abilityData)[1];
            float newSize = MathHelper.linear(4, 0, (float) (-chargeTime) / 60);

            entity.setStreamSize(newSize);
            entity.getChild().setStreamSize(newSize);
        }
        bender.player.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIONARY.get(), 1, 1, false, false, false));
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);

        WaterJetEntity entity = (WaterJetEntity) ((Object[]) bender.abilityData)[1];
        bender.abilityData = null;
        if (entity == null) {
            return;
        }
        entity.discard();
        entity.getChild().discard();
    }

}
