package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.BOOMERANGENTITY;
import static dev.saperate.elementals.entities.ElementalEntities.DIRTBOTTLEENTITY;
import static dev.saperate.elementals.items.ElementalItems.BOOMERANG_ITEM;
import static dev.saperate.elementals.items.ElementalItems.DIRT_BOTTLE_ITEM;

public class BoomerangEntity extends PersistentProjectileEntity {
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
                Vec3 dir = this.getPos().subtract(startingPos).normalize().multiply(-0.5);
                setDeltaMovement(dir);
            }

            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof ItemEntity);
            if (hit instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() != null) {
                tickCount = 21;
                Vec3 dir = this.getPos().subtract(startingPos).normalize().multiply(-0.5);
                setDeltaMovement(dir);
            }
        } else if (tickCount == 20) {
            Vec3 dir = this.getPos().subtract(startingPos).normalize().multiply(-0.5);
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
        ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), BOOMERANG_ITEM.getDefaultStack());
        level().addFreshEntity(itemEntity);
        discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
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
                    ((LivingEntity) owner).onAttacking(living);
                }
            }
            living.hurt(damageSource, 4);
        }
    }

    @Override
    public void onPlayerCollision(Player player) {
        if (player == getOwner()) {
            super.onPlayerCollision(player);
        }
    }


    @Override
    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(BOOMERANG_ITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(BOOMERANG_ITEM);
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putDouble("startX", startingPos.x);
        nbt.putDouble("startY", startingPos.y);
        nbt.putDouble("startZ", startingPos.z);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        startingPos = new Vec3(
                nbt.getDouble("startX"),
                nbt.getDouble("startY"),
                nbt.getDouble("startZ")
        );

        super.readCustomDataFromNbt(nbt);
    }

    public boolean getInGround() {
        return inGround;
    }

}