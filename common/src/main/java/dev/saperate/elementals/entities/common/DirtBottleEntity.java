package dev.saperate.elementals.entities.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.entities.ElementalEntities.DIRTBOTTLEENTITY;
import static dev.saperate.elementals.items.ElementalsItems.DIRT_BOTTLE_ITEM;

public class DirtBottleEntity extends ThrowableItemProjectile {
    private final Level world = level();

    public DirtBottleEntity(EntityType<DirtBottleEntity> entityType, Level world) {
        super(entityType, world);
    }

    public DirtBottleEntity(Level world, LivingEntity owner) {
        super(DIRTBOTTLEENTITY, owner, world);
    }

    public DirtBottleEntity(Level world, Vec3 position) {
        super(DIRTBOTTLEENTITY, position.x, position.y, position.z, world);
    }


    @Override
    protected Item getDefaultItem() {
        return DIRT_BOTTLE_ITEM.get();
    }


    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity hit = entityHitResult.getEntity();
        BlockPos bPos = hit.getOnPos();

        if (!world.getBlockState(bPos).isAir() && !world.getBlockState(bPos).liquid()) {
            bPos = bPos.above();
        }
        if (world.getBlockState(bPos).isAir() || world.getBlockState(bPos).liquid()) {
            level().setBlockAndUpdate(
                    bPos,
                    Blocks.DIRT.defaultBlockState());
        }

        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (hitResult.getType().equals(HitResult.Type.ENTITY)) {
            return;
        }
        BlockPos bPos = new BlockPos(getBlockX(), (int) Math.round(getY()), getBlockZ());
        FallingBlockEntity.fall(world, bPos, Blocks.DIRT.defaultBlockState());
        discard();
    }
    
    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(DIRT_BOTTLE_ITEM.get());
        CompoundTag tag = new CompoundTag();
        tag.putUUID("EntityUUID", this.getUUID());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }
}