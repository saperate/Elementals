package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.SpiritProjectionStatusEffect;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.utils.MathHelper;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;

public class AbilityAir4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        ServerPlayerEntity plr = (ServerPlayerEntity) bender.player;
        PlayerData plrData = PlayerData.get(plr);
        if (!plrData.canUseUpgrade("airSpiritProjection")) {
            bender.setCurrAbility(null);
            return;
        }

        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        DecoyPlayerEntity decoy = new DecoyPlayerEntity(plr.getWorld(), plr);
        int range = 5;
        if (plrData.canUseUpgrade("airSpiritProjectionRangeIV")) {
            range = 25;
        } else if (plrData.canUseUpgrade("airSpiritProjectionRangeIII")) {
            range = 20;
        } else if (plrData.canUseUpgrade("airSpiritProjectionRangeII")) {
            range = 15;
        } else if (plrData.canUseUpgrade("airSpiritProjectionRangeI")) {
            range = 10;
        }
        decoy.setRange(range);

        decoy.setCustomName(plr.getName());

        decoy.equipStack(EquipmentSlot.HEAD, plr.getEquippedStack(EquipmentSlot.HEAD));
        decoy.equipStack(EquipmentSlot.CHEST, plr.getEquippedStack(EquipmentSlot.CHEST));
        decoy.equipStack(EquipmentSlot.LEGS, plr.getEquippedStack(EquipmentSlot.LEGS));
        decoy.equipStack(EquipmentSlot.FEET, plr.getEquippedStack(EquipmentSlot.FEET));
        decoy.equipStack(EquipmentSlot.MAINHAND, plr.getEquippedStack(EquipmentSlot.MAINHAND));
        decoy.equipStack(EquipmentSlot.OFFHAND, plr.getEquippedStack(EquipmentSlot.OFFHAND));

        decoy.setYaw(plr.getYaw());
        decoy.setHeadYaw(plr.getHeadYaw());
        decoy.setPitch(plr.getPitch());

        decoy.setVelocity(plr.getVelocity());
        decoy.fallDistance = plr.fallDistance;
        decoy.setHealth(plr.getHealth());
        decoy.setFireTicks(plr.getFireTicks());
        decoy.setOnFire(plr.isOnFire());

        for (StatusEffectInstance effect : plr.getStatusEffects()) {
            decoy.addStatusEffect(effect);
        }

        plr.getWorld().spawnEntity(decoy);


        bender.abilityData = new Object[]{plr.interactionManager.getGameMode(), decoy};

        bender.player.addStatusEffect(
                new StatusEffectInstance(SPIRIT_PROJECTION_EFFECT,
                        -1,
                        SpiritProjectionStatusEffect.convertGameModeToAmplifier(plr.interactionManager.getGameMode()),
                        false, false, true)
        );

        plr.changeGameMode(GameMode.SPECTATOR);
        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        HitResult hit = SapsUtils.raycastFull(bender.player, 5, false);
        if (hit instanceof EntityHitResult eHit) {
            assert bender.abilityData != null;
            if (eHit.getEntity().equals(((Object[]) bender.abilityData)[1])) {
                onRemove(bender);
            }
        }
    }


    @Override
    public void onTick(Bender bender) {
        Object[] data = (Object[]) bender.abilityData;
        DecoyPlayerEntity decoy = (DecoyPlayerEntity) data[1];

        preventOwnerFromGoingFar(bender, decoy, decoy.getRange());
    }

    private static void preventOwnerFromGoingFar(Bender bender, DecoyPlayerEntity decoy, int range) {
        Vec3d direction = decoy.getPos().subtract(bender.player.getPos());
        double distance = direction.length();
        if (distance > range) {
            if (distance > range * 10) {
                bender.player.teleport(decoy.getX(), decoy.getY(), decoy.getZ());
            }


            direction = direction.multiply(distance - range).multiply(0.1f);


            double damping =  0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / range));
            direction = MathHelper.clampVector(direction.multiply(damping),-10,10);


            bender.player.addVelocity(direction.x,direction.y,direction.z);
            bender.player.move(MovementType.SELF, bender.player.getVelocity());
            bender.player.velocityModified = true;
            
        }
    }

    @Override
    public void onAbilityPress(Bender bender, int keyIndex) {
        if(keyIndex == 3)
            onRemove(bender);
    }
    @Override
    public void onRemove(Bender bender) {
        Object[] data = (Object[]) bender.abilityData;

        GameMode gm = (GameMode) data[0];
        if (gm != null) {
            ((ServerPlayerEntity) bender.player).changeGameMode(gm);
        }

        DecoyPlayerEntity decoy = (DecoyPlayerEntity) data[1];
        if (decoy != null) {
            bender.player.teleport(decoy.getX(), decoy.getY(), decoy.getZ());
            bender.player.setHealth(decoy.getHealth());
            bender.player.setAir(decoy.getAir());
            bender.player.setFireTicks(decoy.getFireTicks());
            bender.player.setOnFire(decoy.isOnFire());
            for (StatusEffectInstance effect : decoy.getStatusEffects()) {
                bender.player.addStatusEffect(effect);
            }

            decoy.discard();
        }

        bender.player.removeStatusEffect(SPIRIT_PROJECTION_EFFECT);
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}
