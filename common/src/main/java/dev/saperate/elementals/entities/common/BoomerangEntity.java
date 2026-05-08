package dev.saperate.elementals.entities.common;


import dev.saperate.elementals.items.ElementalsItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.entities.ElementalEntities.BOOMERANGENTITY;
import static dev.saperate.elementals.items.ElementalsItems.BOOMERANG_ITEM;

public class BoomerangEntity extends AbstractArrow {
    public Vec3 startingPos;
    public int time = 0;

    public BoomerangEntity(EntityType<BoomerangEntity> entityType, Level world) {
        super(entityType, world);
    }

    public BoomerangEntity(Level world, Player owner, Vec3 startingPos) {
        super(BOOMERANGENTITY, world);
        setOwner(owner);
        this.startingPos = startingPos;
        setNoGravity(true);
        setSilent(true);
    }

    public BoomerangEntity(Level world, Vec3 startingPos, ItemStack stack) {
        super(BOOMERANGENTITY, startingPos.x, startingPos.y, startingPos.z, world, stack, null);
        this.startingPos = startingPos;
        setNoGravity(true);
        setSilent(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        if (startingPos == null) {
            discard();
            return;
        }


        if (tickCount < 20) {
            if (inGround) {
                inGround = false;
                inGroundTime = 0;
                Vec3 dir = this.position().subtract(startingPos).normalize().scale(-0.5);
                setDeltaMovement(dir);
            }

            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, entity -> entity instanceof ItemEntity);
            if (hit instanceof EntityHitResult entityHitResult) {
                tickCount = 21;
                Vec3 dir = this.position().subtract(startingPos).normalize().scale(-0.5);
                setDeltaMovement(dir);
            }
        } else if (tickCount == 20) {
            Vec3 dir = this.position().subtract(startingPos).normalize().scale(-0.5);
            setDeltaMovement(dir);
        } else if (tickCount == 70) {
            setNoGravity(false);
        } else if (tickCount > 70 && inGround && getOwner() == null) {
            dropBoomerang();
        } else if (tickCount > 200 && inGround) {
            dropBoomerang();
        }
    }

    public void dropBoomerang(){
        ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), BOOMERANG_ITEM.getDefaultInstance());
        level().addFreshEntity(itemEntity);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity owner = getOwner();

        if (entityHitResult.getEntity().equals(owner)) {
            if (!tryPickup((Player) owner)) {
                tickCount = 70;
            } else {
                discard();
            }
            return;
        } else if (entityHitResult.getEntity() instanceof Player player && owner == null) {
            if (!tryPickup(player)) {
                tickCount = 70;
            } else {
                discard();
            }
            return;
        }
        if (tickCount <= 20) {
            tickCount = 20;
        } else {
            tickCount = 70;
        }

        if (entityHitResult.getEntity() instanceof LivingEntity living) {
            DamageSource damageSource;
            if (owner == null) {
                damageSource = this.damageSources().arrow(this, this);
            } else {
                damageSource = this.damageSources().arrow(this, owner);
                if (owner instanceof LivingEntity) {
                    ((LivingEntity) owner).setLastHurtMob(living);
                }
            }
            living.hurt(damageSource, 4);
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (player == getOwner()) {
            super.playerTouch(player);
        }
    }


    @Override
    public ItemStack getPickupItem() {
        ItemStack stack = new ItemStack(BOOMERANG_ITEM);
        CompoundTag tag = new CompoundTag();
        tag.putUUID("EntityUUID", this.getUUID());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(BOOMERANG_ITEM);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putDouble("startX", startingPos.x);
        nbt.putDouble("startY", startingPos.y);
        nbt.putDouble("startZ", startingPos.z);
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        startingPos = new Vec3(
                nbt.getDouble("startX"),
                nbt.getDouble("startY"),
                nbt.getDouble("startZ")
        );

        super.readAdditionalSaveData(nbt);
    }

    public boolean getInGround() {
        return inGround;
    }

}