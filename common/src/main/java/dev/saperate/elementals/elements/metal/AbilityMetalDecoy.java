package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.SpiritProjectionStatusEffect;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.MobEffectInstance;
import net.minecraft.entity.effect.MobEffects;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.GameMode;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.List;

public class AbilityMetalDecoy implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        ServerPlayerEntity plr = (ServerPlayerEntity) bender.player;
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

        decoy.equipStack(EquipmentSlot.HEAD, ElementalItems.METAL_HELMET.getDefaultStack());
        decoy.equipStack(EquipmentSlot.CHEST, ElementalItems.METAL_CHESTPLATE.getDefaultStack());
        decoy.equipStack(EquipmentSlot.LEGS, ElementalItems.METAL_LEGGINGS.getDefaultStack());
        decoy.equipStack(EquipmentSlot.FEET, ElementalItems.METAL_BOOTS.getDefaultStack());
        decoy.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        decoy.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        decoy.setYaw(plr.getYaw());
        decoy.setHeadYaw(plr.getHeadYaw());
        decoy.setPitch(plr.getPitch());

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

        //doesnt work, idk why
//        EntityAnimationS2CPacket entityAnimationS2CPacket = new EntityAnimationS2CPacket(decoy, 0);
//        ServerChunkManager serverChunkManager = ((ServerWorld) decoy.level()).getChunkManager();
//        serverChunkManager.sendToNearbyPlayers(decoy, entityAnimationS2CPacket);

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
                bender.abilityData = packAbilityData(getDecoy(bender), decoy.tickCount, ((BlockHitResult) hit).getOnPos(), getShouldRotate(bender));
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

        player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 21, 1, false, false, false));

        if (getShouldRotate(bender)) {
            decoy.setYaw(player.getYaw());
            decoy.setHeadYaw(player.getHeadYaw());
            decoy.setPitch(player.getPitch());
        }

        if (player.isSprinting()) {

            float speed = 0.1f;
            Vec3 velocity = SapsUtils.getEntityLookVector(decoy, 1)
                    .subtract(decoy.getEyePos())
                    .normalize().multiply(speed, 0, speed).add(0, decoy.getDeltaMovement().y, 0);
            int range = 25;
            if (bender.plrData.canUseUpgrade("metalDecoyRangeII")) {
                range = 75;
            } else if (bender.plrData.canUseUpgrade("metalDecoyRangeI")) {
                range = 50;
            }

            float distanceToDecoy = player.distanceTo(decoy);
            if (distanceToDecoy > range) {
                Vec3 dirToPlayer = player.getPos().subtract(decoy.getPos());
                if (dirToPlayer.dotProduct(velocity) < 1) {
                    velocity = velocity.multiply(Math.min(1, (0.1) / (distanceToDecoy - range)));
                }
            }


            decoy.setDeltaMovement(velocity);
            decoy.move(MoverType.SELF, decoy.getDeltaMovement());

        }
        if (player.isInSneakingPose() && decoy.isOnGround()) {
            decoy.setDeltaMovement(0, 0.5, 0);
            decoy.move(MoverType.SELF, decoy.getDeltaMovement());
        }

        if (getStartMiningAge(bender) >= 0) {

            HitResult hit = SapsUtils.raycastFull(decoy, 5, false, Entity::isAlive);

            if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
                return;
            }

            BlockPos prevMiningPos = getMiningPos(bender);
            BlockPos currMiningPos = ((BlockHitResult) hit).getOnPos();
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
    
    
    /* TODO put this in metal armor
    public void equipArmorItem(Entity decoy, EquipmentSlot slot, ItemStack stack){
        stack.addEnchantment(Enchantments.PROTECTION,4);
        if(false) {//upgrade that adds spikes to your armor
            stack.addEnchantment(Enchantments.THORNS, 1);
        }
        decoy.equipStack(slot, stack);
    }
     */

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

    public void attack(Entity target, DecoyPlayerEntity decoy) {
        if (target.isAttackable()) {
            if (!target.handleAttack(decoy)) {
                float f = (float) 1;
                ItemStack itemStack = decoy.getEquippedStack(EquipmentSlot.MAINHAND);
                DamageSource damageSource = decoy.damageSources().playerAttack(decoy.getOwner());
                float g = f;
                float h = decoy.getOwner().getAttackCooldownProgress(0.5F);
                f *= 0.2F + h * h * 0.8F;
                g *= h;
                decoy.getOwner().resetLastAttackedTicks();

                if (f > 0.0F || g > 0.0F) {
                    boolean bl = h > 0.9F;

                    boolean bl3 = bl && decoy.fallDistance > 0.0F && !decoy.isOnGround() && !decoy.isClimbing() && !decoy.isTouchingWater() && !decoy.hasStatusEffect(MobEffects.BLINDNESS) && !decoy.hasVehicle() && target instanceof LivingEntity && !decoy.isSprinting();
                    if (bl3) {
                        f *= 1.5F;
                    }

                    float i = f + g;
                    boolean bl4 = false;
                    double d = (double) (decoy.horizontalSpeed - decoy.prevHorizontalSpeed);
                    if (bl && !bl3 && decoy.isOnGround() && d < (double) decoy.getMovementSpeed()) {
                        ItemStack itemStack2 = decoy.getStackInHand(Hand.MAIN_HAND);
                        if (itemStack2.getItem() instanceof SwordItem) {
                            bl4 = true;
                        }
                    }

                    float j = 0.0F;
                    if (target instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) target;
                        j = livingEntity.getHealth();
                    }

                    Vec3 vec3d = target.getDeltaMovement();
                    boolean bl5 = target.hurt(damageSource, i * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
                    if (bl5) {
                        float k = 1;
                        if (k > 0.0F) {
                            if (target instanceof LivingEntity) {
                                LivingEntity livingEntity2 = (LivingEntity) target;
                                livingEntity2.takeKnockback((double) (k * 0.5F), (double) MathHelper.sin(decoy.getYaw() * 0.017453292F), (double) (-MathHelper.cos(decoy.getYaw() * 0.017453292F)));
                            } else {
                                target.addDeltaMovement((double) (-MathHelper.sin(decoy.getYaw() * 0.017453292F) * k * 0.5F), 0.1, (double) (MathHelper.cos(decoy.getYaw() * 0.017453292F) * k * 0.5F));
                            }

                            decoy.setDeltaMovement(decoy.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                            decoy.setSprinting(false);
                        }

                        LivingEntity livingEntity3;
                        if (bl4) {
                            float l = 1.0F + f;
                            List<LivingEntity> list = decoy.level().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
                            Iterator var20 = list.iterator();

                            label177:
                            while (true) {
                                do {
                                    do {
                                        do {
                                            do {
                                                if (!var20.hasNext()) {
                                                    decoy.level().playSound((Player) null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, decoy.getSoundCategory(), 1.0F, 1.0F);
                                                    break label177;
                                                }

                                                livingEntity3 = (LivingEntity) var20.next();
                                            } while (livingEntity3 == decoy);
                                        } while (livingEntity3 == target);
                                    } while (decoy.isTeammate(livingEntity3));
                                } while (livingEntity3 instanceof ArmorStandEntity && ((ArmorStandEntity) livingEntity3).isMarker());

                                if (decoy.squaredDistanceTo(livingEntity3) < 9.0) {
                                    float m = l;
                                    livingEntity3.takeKnockback(0.4000000059604645, (double) MathHelper.sin(decoy.getYaw() * 0.017453292F), (double) (-MathHelper.cos(decoy.getYaw() * 0.017453292F)));
                                    livingEntity3.hurt(damageSource, m);
                                    Level var24 = decoy.level();
                                    if (var24 instanceof ServerWorld) {
                                        ServerWorld serverWorld = (ServerWorld)var24;
                                        EnchantmentHelper.onTargetDamaged(serverWorld ,livingEntity3, damageSource);
                                    }
                                }
                            }
                        }

                        if (target instanceof ServerPlayerEntity && target.velocityModified) {
                            ((ServerPlayerEntity) target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                            target.velocityModified = false;
                            target.setDeltaMovement(vec3d);
                        }

                        if (bl3) {
                            decoy.level().playSound((Player) null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, decoy.getSoundCategory(), 1.0F, 1.0F);
                        }

                        if (!bl3 && !bl4) {
                            if (bl) {
                                decoy.level().playSound((Player) null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, decoy.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                decoy.level().playSound((Player) null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, decoy.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }


                        decoy.onAttacking(target);
                        Entity entity = target;
                        if (target instanceof EnderDragonPart) {
                            entity = ((EnderDragonPart) target).owner;
                        }

                        boolean bl6 = false;
                        Level var33 = decoy.level();
                        if (var33 instanceof ServerWorld) {
                            ServerWorld serverWorld2 = (ServerWorld) var33;
                            if (entity instanceof LivingEntity) {
                                livingEntity3 = (LivingEntity) entity;
                                itemStack.postHit(livingEntity3, decoy.getOwner());
                            }

                            EnchantmentHelper.onTargetDamaged((ServerWorld) decoy.level() ,target, damageSource);
                        }

                        if (target instanceof LivingEntity) {
                            float n = j - ((LivingEntity) target).getHealth();
                            decoy.getOwner().increaseStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
                            if (decoy.level() instanceof ServerWorld && n > 2.0F) {
                                int o = (int) ((double) n * 0.5);
                                ((ServerWorld) decoy.level()).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), o, 0.1, 0.0, 0.1, 0.2);
                            }
                        }

                        decoy.getOwner().addExhaustion(0.1F);
                    } else {
                        decoy.level().playSound((Player) null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, decoy.getSoundCategory(), 1.0F, 1.0F);
                    }
                }

            }
        }
    }

}
