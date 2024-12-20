package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import dev.saperate.elementals.utils.MathHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
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

        PlayerEntity player = bender.player;
        Vector3f pos = WaterElement.canBend(player, true);

        if (pos != null) {
            bender.abilityData = new Object[]{60, null}; //null is a placeholder, an entity will be there
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
    }

    @Override
    public void onTick(Bender bender) {
        int chargeTime = (int) ((Object[]) bender.abilityData)[0];
        ((Object[]) bender.abilityData)[0] = chargeTime - 1;

        if (chargeTime == 0) {//Just got done charging
            PlayerEntity player = bender.player;
            Vec3d pos = getEntityLookVector(player, .5f);

            WaterJetEntity parent = new WaterJetEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            ((Object[]) bender.abilityData)[1] = parent;
            parent.setStreamSize(4);
            parent.setRange(20);
            player.getWorld().spawnEntity(parent);

            WaterJetEntity child = new WaterJetEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            parent.setChild(child);
            parent.setStreamSize(4);
            parent.setRange(20);
            player.getWorld().spawnEntity(child);

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
            PlayerEntity player = bender.player;
            serverSummonParticles((ServerWorld) player.getWorld(),
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
        bender.player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.STATIONARY, 1, 1, false, false, false));
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
