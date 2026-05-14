package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AbilityMetalDecoy implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        ServerPlayer plr = (ServerPlayer) bender.player;
        PlayerData plrData = PlayerData.get(plr);

        if (!plrData.canUseUpgrade("metalDecoy") ||
                !bender.reduceChi(40) || !MetalElement.canBend(plr, 63)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        DecoyPlayerEntity decoy = new DecoyPlayerEntity(bender.player.level(), bender.player);

        decoy.setCustomName(plr.getName());

        decoy.setItemSlot(EquipmentSlot.HEAD, ElementalsItems.METAL_HELMET.get().getDefaultInstance());
        decoy.setItemSlot(EquipmentSlot.CHEST, ElementalsItems.METAL_CHESTPLATE.get().getDefaultInstance());
        decoy.setItemSlot(EquipmentSlot.LEGS, ElementalsItems.METAL_LEGGINGS.get().getDefaultInstance());
        decoy.setItemSlot(EquipmentSlot.FEET, ElementalsItems.METAL_BOOTS.get().getDefaultInstance());
        decoy.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        decoy.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        decoy.setYRot(plr.getYRot());
        decoy.setYHeadRot(plr.getYHeadRot());
        decoy.setXRot(plr.getXRot());

        decoy.setHealth(40);
        decoy.setPos(plr.getX(), plr.getY() + 1, plr.getZ());
        decoy.setFocusCamera(true);
        decoy.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 9999999, 0, false, false, false));

        plr.level().addFreshEntity(decoy);


        bender.abilityData = packAbilityData(decoy, -100, null, false);

        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        DecoyPlayerEntity decoy = getDecoy(bender);
        HitResult hit = SapsUtils.raycastFull(decoy, 5, false, Entity::isAlive);
        if (hit == null)
            return;
        if (hit.getType().equals(HitResult.Type.ENTITY) && !started) {
            Entity eHit = ((EntityHitResult) hit).getEntity();
            int damage = 2;
            PlayerData plrData = bender.plrData;
            if (plrData.canUseUpgrade("metalDecoyDamageII")) {
                damage = 6;
            } else if (plrData.canUseUpgrade("metalDecoyDamageI")) {
                damage = 4;
            }
            eHit.hurt(bender.player.damageSources().playerAttack(bender.player), damage);
        } else if (hit.getType().equals(HitResult.Type.BLOCK)) {
            if (started) {
                bender.abilityData = packAbilityData(getDecoy(bender), decoy.tickCount, ((BlockHitResult) hit).getBlockPos(), getShouldRotate(bender));
            } else {
                bender.abilityData = packAbilityData(getDecoy(bender), -100, null, getShouldRotate(bender));
            }
        }
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        bender.abilityData = packAbilityData(getDecoy(bender), getStartMiningAge(bender), getMiningPos(bender), started);
    }

    @Override
    public void onTick(Bender bender) {
        DecoyPlayerEntity decoy = getDecoy(bender);
        Player player = bender.player;

        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 21, 1, false, false, false));

        if (getShouldRotate(bender)) {
            decoy.setYRot(player.getYRot());
            decoy.setYHeadRot(player.getYHeadRot());
            decoy.setXRot(player.getXRot());
        }

        if (player.isSprinting()) {

            float speed = 0.1f;
            Vec3 velocity = SapsUtils.getEntityLookVector(decoy, 1)
                    .subtract(decoy.getEyePosition())
                    .normalize().multiply(speed, 0, speed).add(0, decoy.getDeltaMovement().y, 0);
            int range = 25;
            if (bender.plrData.canUseUpgrade("metalDecoyRangeII")) {
                range = 75;
            } else if (bender.plrData.canUseUpgrade("metalDecoyRangeI")) {
                range = 50;
            }

            float distanceToDecoy = player.distanceTo(decoy);
            if (distanceToDecoy > range) {
                Vec3 dirToPlayer = player.position().subtract(decoy.position());
                if (dirToPlayer.dot(velocity) < 1) {
                    velocity = velocity.scale(Math.min(1, (0.1) / (distanceToDecoy - range)));
                }
            }


            decoy.setDeltaMovement(velocity);
            decoy.move(MoverType.SELF, decoy.getDeltaMovement());

        }
        if (player.isCrouching() && decoy.onGround()) {
            decoy.setDeltaMovement(0, 0.5, 0);
            decoy.move(MoverType.SELF, decoy.getDeltaMovement());
        }

        if (getStartMiningAge(bender) >= 0) {

            HitResult hit = SapsUtils.raycastFull(decoy, 5, false, Entity::isAlive);

            if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
                return;
            }

            BlockPos prevMiningPos = getMiningPos(bender);
            BlockPos currMiningPos = ((BlockHitResult) hit).getBlockPos();
            if (prevMiningPos == null || !prevMiningPos.equals(currMiningPos)) {
                bender.abilityData = packAbilityData(
                        decoy,
                        decoy.tickCount,
                        currMiningPos,
                        getShouldRotate(bender));
                return;
            }

            int miningSpeed = 60;
            PlayerData plrData = bender.plrData;
            if (plrData.canUseUpgrade("metalDecoyDamageII")) {
                miningSpeed = 15;
            } else if (plrData.canUseUpgrade("metalDecoyDamageI")) {
                miningSpeed = 30;
            }
            SapsUtils.mineBlock(currMiningPos, decoy.level(), decoy.getId(), decoy.tickCount, getStartMiningAge(bender), miningSpeed);
        }
    }

    @Override
    public void onAbilityPress(Bender bender, int keyIndex) {
        if (keyIndex == 3)
            onRemove(bender);
    }

    @Override
    public void onRemove(Bender bender) {
        getDecoy(bender).discard();
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

    @Override
    public boolean shouldImmobilizePlayer(Player player) {
        return true;
    }
    
    ///Ability data stuff, cleaner to put it all in their own methods
    public Object packAbilityData(DecoyPlayerEntity decoy, int startMiningAge, BlockPos miningPos, boolean shouldRotate) {
        return new Object[]{decoy, startMiningAge, miningPos, shouldRotate};
    }

    public DecoyPlayerEntity getDecoy(Bender bender) {
        Object[] data = (Object[]) bender.abilityData;
        if (data != null && data[0] instanceof DecoyPlayerEntity entity) {
            return entity;
        }
        return null;
    }

    public int getStartMiningAge(Bender bender) {
        Object[] data = (Object[]) bender.abilityData;
        if (data != null) {
            return (int) data[1];
        }
        return 0;
    }

    public BlockPos getMiningPos(Bender bender) {
        Object[] data = (Object[]) bender.abilityData;
        if (data != null) {
            return (BlockPos) data[2];
        }
        return null;
    }

    public boolean getShouldRotate(Bender bender) {
        Object[] data = (Object[]) bender.abilityData;
        if (data != null) {
            return (boolean) data[3];
        }
        return false;
    }

}
