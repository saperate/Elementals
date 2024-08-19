package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.BOOMERANGENTITY;
import static dev.saperate.elementals.entities.ElementalEntities.DIRTBOTTLEENTITY;
import static dev.saperate.elementals.items.ElementalItems.BOOMERANG_ITEM;
import static dev.saperate.elementals.items.ElementalItems.DIRT_BOTTLE_ITEM;

public class BoomerangEntity extends PersistentProjectileEntity {
    public Vec3d startingPos;
    public int time = 0;

    public BoomerangEntity(EntityType<BoomerangEntity> entityType, World world) {
        super(entityType, world);
    }

    public BoomerangEntity(World world, PlayerEntity owner, Vec3d startingPos) {
        super(BOOMERANGENTITY, owner, world);
        this.startingPos = startingPos;
        setNoGravity(true);
        setSilent(true);
    }


    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) {
            return;
        }
        if (startingPos == null) {
            discard();
            return;
        }

        if (age < 20) {
            if(inGround){
                inGround = false;
                inGroundTime = 0;
                Vec3d dir = this.getPos().subtract(startingPos).normalize().multiply(-0.5);
                setVelocity(dir);
            }

            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof ItemEntity);
            if (hit instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() != null) {
                age = 21;
                Vec3d dir = this.getPos().subtract(startingPos).normalize().multiply(-0.5);
                setVelocity(dir);
            }
        } else if (age == 20) {
            Vec3d dir = this.getPos().subtract(startingPos).normalize().multiply(-0.5);
            setVelocity(dir);
        } else if (age == 70) {
            setNoGravity(false);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity owner = getOwner();

        if (entityHitResult.getEntity().equals(owner)) {
            if (!tryPickup((PlayerEntity) owner)) {
                age = 70;
            } else {
                discard();
            }
            return;
        }
        if (age <= 20) {
            age = 20;
        } else {
            age = 70;
        }

        if(entityHitResult.getEntity() instanceof LivingEntity living){
            DamageSource damageSource;
            if (owner == null) {
                damageSource = this.getDamageSources().arrow(this, this);
            } else {
                damageSource = this.getDamageSources().arrow(this, owner);
                if (owner instanceof LivingEntity) {
                    ((LivingEntity)owner).onAttacking(living);
                }
            }
            living.damage(damageSource,1);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (player == getOwner()) {
            super.onPlayerCollision(player);
        }
    }


    @Override
    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(BOOMERANG_ITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid());
        stack.setNbt(tag);
        return stack;
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
        startingPos = new Vec3d(
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