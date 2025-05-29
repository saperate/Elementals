package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.SpiritProjectionStatusEffect;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.List;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;

public class AbilityMetalDecoy implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        ServerPlayerEntity plr = (ServerPlayerEntity) bender.player;
        PlayerData plrData = PlayerData.get(plr);
        if (!plrData.canUseUpgrade("metalDecoy") && false) {
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
        DecoyPlayerEntity decoy = new DecoyPlayerEntity(bender.player.getWorld(),bender.player);

        decoy.setCustomName(plr.getName());

        decoy.equipStack(EquipmentSlot.HEAD,  ElementalItems.EARTH_HELMET.getDefaultStack());
        decoy.equipStack(EquipmentSlot.CHEST, ElementalItems.EARTH_CHESTPLATE.getDefaultStack());
        decoy.equipStack(EquipmentSlot.LEGS,  ElementalItems.EARTH_LEGGINGS.getDefaultStack());
        decoy.equipStack(EquipmentSlot.FEET,  ElementalItems.EARTH_BOOTS.getDefaultStack());
        decoy.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        decoy.equipStack(EquipmentSlot.OFFHAND,  ItemStack.EMPTY);

        //TODO save player params so we can lock them and make the decoy mimic movements
        decoy.setYaw(plr.getYaw());
        decoy.setHeadYaw(plr.getHeadYaw());
        decoy.setPitch(plr.getPitch());

        decoy.setHealth(20);
        decoy.setPos(plr.getX(),plr.getY(),plr.getZ());
        plr.getWorld().spawnEntity(decoy);


        bender.abilityData = packAbilityData(decoy, 0,0,0,false);

        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        DecoyPlayerEntity decoy = getDecoy(bender);
        HitResult hit = SapsUtils.raycastEntity(decoy,5,Entity::isAlive);
        if(hit == null || !hit.getType().equals(HitResult.Type.ENTITY))
            return;
        attack(((EntityHitResult) hit).getEntity(), decoy);
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        bender.abilityData = packAbilityData(getDecoy(bender),getDeltaYaw(bender),getDeltaHeadYaw(bender), getDeltaPitch(bender),started);
    }

    @Override
    public void onTick(Bender bender) {
        //TODO an upgrade to see with seismic sense which would center the camera around the decoy could be cool
        DecoyPlayerEntity decoy = getDecoy(bender);
        PlayerEntity player = bender.player;

        if(getShouldRotate(bender)){
            decoy.setYaw(player.getYaw() + getDeltaYaw(bender));
            decoy.setHeadYaw(player.getHeadYaw() + getDeltaHeadYaw(bender));
            decoy.setPitch(player.getPitch() + getDeltaPitch(bender));
        }
        
        if(player.isSprinting()){


            Vec3d velocity = SapsUtils.getEntityLookVector(decoy, 1)
                    .subtract(decoy.getEyePos())
                    .normalize().multiply(0.1,0,0.1);
            decoy.setVelocity(velocity);
            decoy.move(MovementType.SELF, decoy.getVelocity());
        }
        if(player.isSneaking() ){
            decoy.setVelocity(0,0.25,0);
            decoy.move(MovementType.SELF, decoy.getVelocity());
        }
    }

    @Override
    public void onRemove(Bender bender) {
        getDecoy(bender).discard();
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

    @Override
    public boolean shouldImmobilizePlayer(PlayerEntity player) {
        return true;
    }

    ///Ability data stuff, cleaner to put it all in their own methods
    public Object packAbilityData(DecoyPlayerEntity decoy, float deltaYaw, float deltaHeadYaw, float deltaPitch, boolean shouldRotate){
        return new Object[]{decoy,deltaYaw,deltaHeadYaw,deltaPitch,shouldRotate};
    }

    public DecoyPlayerEntity getDecoy(Bender bender){
        Object[] data = (Object[]) bender.abilityData;
        if(data != null && data[0] instanceof DecoyPlayerEntity entity){
            return entity;
        }
        return null;
    }

    public float getDeltaYaw(Bender bender){
        Object[] data = (Object[]) bender.abilityData;
        if(data != null){
            return (float) data[1];
        }
        return 0;
    }

    public float getDeltaHeadYaw(Bender bender){
        Object[] data = (Object[]) bender.abilityData;
        if(data != null){
            return (float) data[2];
        }
        return 0;
    }

    public float getDeltaPitch(Bender bender){
        Object[] data = (Object[]) bender.abilityData;
        if(data != null){
            return (float) data[3];
        }
        return 0;
    }

    public boolean getShouldRotate(Bender bender){
        Object[] data = (Object[]) bender.abilityData;
        if(data != null){
            return (boolean) data[4];
        }
        return false;
    }

    public void attack(Entity target, DecoyPlayerEntity decoy) {
        if (target.isAttackable()) {
            if (!target.handleAttack(decoy)) {
                float f = (float) 1;
                ItemStack itemStack = decoy.getEquippedStack(EquipmentSlot.MAINHAND);
                DamageSource damageSource = decoy.getDamageSources().playerAttack(decoy.getOwner());
                float g = f;
                float h = decoy.getOwner().getAttackCooldownProgress(0.5F);
                f *= 0.2F + h * h * 0.8F;
                g *= h;
                decoy.getOwner().resetLastAttackedTicks();

                if (f > 0.0F || g > 0.0F) {
                    boolean bl = h > 0.9F;

                    boolean bl3 = bl && decoy.fallDistance > 0.0F && !decoy.isOnGround() && !decoy.isClimbing() && !decoy.isTouchingWater() && !decoy.hasStatusEffect(StatusEffects.BLINDNESS) && !decoy.hasVehicle() && target instanceof LivingEntity && !decoy.isSprinting();
                    if (bl3) {
                        f *= 1.5F;
                    }

                    float i = f + g;
                    boolean bl4 = false;
                    double d = (double)(decoy.horizontalSpeed - decoy.prevHorizontalSpeed);
                    if (bl && !bl3 && decoy.isOnGround() && d < (double)decoy.getMovementSpeed()) {
                        ItemStack itemStack2 = decoy.getStackInHand(Hand.MAIN_HAND);
                        if (itemStack2.getItem() instanceof SwordItem) {
                            bl4 = true;
                        }
                    }

                    float j = 0.0F;
                    if (target instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)target;
                        j = livingEntity.getHealth();
                    }

                    Vec3d vec3d = target.getVelocity();
                    boolean bl5 = target.damage(damageSource, i);
                    if (bl5) {
                        float k = 1;
                        if (k > 0.0F) {
                            if (target instanceof LivingEntity) {
                                LivingEntity livingEntity2 = (LivingEntity)target;
                                livingEntity2.takeKnockback((double)(k * 0.5F), (double) MathHelper.sin(decoy.getYaw() * 0.017453292F), (double)(-MathHelper.cos(decoy.getYaw() * 0.017453292F)));
                            } else {
                                target.addVelocity((double)(-MathHelper.sin(decoy.getYaw() * 0.017453292F) * k * 0.5F), 0.1, (double)(MathHelper.cos(decoy.getYaw() * 0.017453292F) * k * 0.5F));
                            }

                            decoy.setVelocity(decoy.getVelocity().multiply(0.6, 1.0, 0.6));
                            decoy.setSprinting(false);
                        }

                        LivingEntity livingEntity3;
                        if (bl4) {
                            float l = 1.0F + f;
                            List<LivingEntity> list = decoy.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
                            Iterator var20 = list.iterator();

                            label177:
                            while(true) {
                                do {
                                    do {
                                        do {
                                            do {
                                                if (!var20.hasNext()) {
                                                    decoy.getWorld().playSound((PlayerEntity)null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, decoy.getSoundCategory(), 1.0F, 1.0F);
                                                    break label177;
                                                }

                                                livingEntity3 = (LivingEntity)var20.next();
                                            } while(livingEntity3 == decoy);
                                        } while(livingEntity3 == target);
                                    } while(decoy.isTeammate(livingEntity3));
                                } while(livingEntity3 instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity3).isMarker());

                                if (decoy.squaredDistanceTo(livingEntity3) < 9.0) {
                                    float m = l;
                                    livingEntity3.takeKnockback(0.4000000059604645, (double)MathHelper.sin(decoy.getYaw() * 0.017453292F), (double)(-MathHelper.cos(decoy.getYaw() * 0.017453292F)));
                                    livingEntity3.damage(damageSource, m);
                                    World var24 = decoy.getWorld();
                                    if (var24 instanceof ServerWorld) {
                                        ServerWorld serverWorld = (ServerWorld)var24;
                                        EnchantmentHelper.onTargetDamaged(decoy, livingEntity3);
                                    }
                                }
                            }
                        }

                        if (target instanceof ServerPlayerEntity && target.velocityModified) {
                            ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                            target.velocityModified = false;
                            target.setVelocity(vec3d);
                        }

                        if (bl3) {
                            decoy.getWorld().playSound((PlayerEntity)null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, decoy.getSoundCategory(), 1.0F, 1.0F);
                        }

                        if (!bl3 && !bl4) {
                            if (bl) {
                                decoy.getWorld().playSound((PlayerEntity)null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, decoy.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                decoy.getWorld().playSound((PlayerEntity)null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, decoy.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }


                        decoy.onAttacking(target);
                        Entity entity = target;
                        if (target instanceof EnderDragonPart) {
                            entity = ((EnderDragonPart)target).owner;
                        }

                        boolean bl6 = false;
                        World var33 = decoy.getWorld();
                        if (var33 instanceof ServerWorld) {
                            ServerWorld serverWorld2 = (ServerWorld)var33;
                            if (entity instanceof LivingEntity) {
                                livingEntity3 = (LivingEntity)entity;
                                itemStack.postHit(livingEntity3, decoy.getOwner());
                            }

                            EnchantmentHelper.onTargetDamaged(decoy, target);
                        }

                        if (target instanceof LivingEntity) {
                            float n = j - ((LivingEntity)target).getHealth();
                            decoy.getOwner().increaseStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
                            if (decoy.getWorld() instanceof ServerWorld && n > 2.0F) {
                                int o = (int)((double)n * 0.5);
                                ((ServerWorld)decoy.getWorld()).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), o, 0.1, 0.0, 0.1, 0.2);
                            }
                        }

                        decoy.getOwner().addExhaustion(0.1F);
                    } else {
                        decoy.getWorld().playSound((PlayerEntity)null, decoy.getX(), decoy.getY(), decoy.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, decoy.getSoundCategory(), 1.0F, 1.0F);
                    }
                }

            }
        }
    }

}
