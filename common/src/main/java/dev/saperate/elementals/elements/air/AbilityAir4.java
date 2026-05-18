package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.effects.SpiritProjectionStatusEffect;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.utils.MathHelper;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


public class AbilityAir4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        ServerPlayer plr = (ServerPlayer) bender.player;
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

        DecoyPlayerEntity decoy = new DecoyPlayerEntity(plr.level(), plr);
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

        decoy.setCustomName(plr.getDisplayName());

        decoy.setItemSlot(EquipmentSlot.HEAD, plr.getItemBySlot(EquipmentSlot.HEAD));
        decoy.setItemSlot(EquipmentSlot.CHEST, plr.getItemBySlot(EquipmentSlot.CHEST));
        decoy.setItemSlot(EquipmentSlot.LEGS, plr.getItemBySlot(EquipmentSlot.LEGS));
        decoy.setItemSlot(EquipmentSlot.FEET, plr.getItemBySlot(EquipmentSlot.FEET));
        decoy.setItemSlot(EquipmentSlot.MAINHAND, plr.getItemBySlot(EquipmentSlot.MAINHAND));
        decoy.setItemSlot(EquipmentSlot.OFFHAND, plr.getItemBySlot(EquipmentSlot.OFFHAND));

        decoy.setYRot(plr.getYRot());
        decoy.setYHeadRot(plr.getYHeadRot());
        decoy.setXRot(plr.getXRot());

        decoy.setDeltaMovement(plr.getDeltaMovement());
        decoy.fallDistance = plr.fallDistance;
        decoy.setHealth(plr.getHealth());
        decoy.igniteForTicks(plr.getRemainingFireTicks());
        decoy.setSharedFlagOnFire(plr.isOnFire());

        for (MobEffectInstance effect : plr.getActiveEffects()) {
            decoy.addEffect(effect);
        }

        plr.level().addFreshEntity(decoy);


        bender.abilityData = new Object[]{plr.gameMode.getGameModeForPlayer(), decoy};

        bender.player.addEffect(
                new MobEffectInstance(ElementalsStatusEffects.SPIRIT_PROJECTION.get(),
                        -1,
                        SpiritProjectionStatusEffect.convertGameModeToAmplifier(plr.gameMode.getGameModeForPlayer()),
                        false, false, true)
        );

        plr.setGameMode(GameType.SPECTATOR);
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
        Vec3 direction = decoy.position().subtract(bender.player.position());
        double distance = direction.length();
        if (distance > range) {
            if (distance > range * 10) {
                bender.player.teleportTo(decoy.getX(), decoy.getY(), decoy.getZ());
            }


            direction = direction.scale(distance - range).scale(0.1f);


            double damping =  0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / range));
            direction = MathHelper.clampVector(direction.scale(damping),-10,10);


            bender.player.addDeltaMovement(new Vec3(direction.x,direction.y,direction.z));
            bender.player.move(MoverType.SELF, bender.player.getDeltaMovement());
            bender.player.hurtMarked = true;
            
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

        GameType gm = (GameType) data[0];
        if (gm != null) {
            ((ServerPlayer) bender.player).setGameMode(gm);
        }

        DecoyPlayerEntity decoy = (DecoyPlayerEntity) data[1];
        if (decoy != null) {
            bender.player.teleportTo(decoy.getX(), decoy.getY(), decoy.getZ());
            bender.player.setHealth(decoy.getHealth());
            bender.player.setAirSupply(decoy.getAirSupply());
            bender.player.setRemainingFireTicks(decoy.getRemainingFireTicks());
            bender.player.setSharedFlagOnFire(decoy.isOnFire());
            for (MobEffectInstance effect : decoy.getActiveEffects()) {
                bender.player.addEffect(effect);
            }

            decoy.discard();
        }

        bender.player.removeEffect(ElementalsStatusEffects.SPIRIT_PROJECTION.get());
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}
