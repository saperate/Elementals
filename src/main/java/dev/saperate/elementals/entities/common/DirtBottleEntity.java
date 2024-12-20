package dev.saperate.elementals.entities.common;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.elementals.entities.ElementalEntities.DIRTBOTTLEENTITY;
import static dev.saperate.elementals.items.ElementalItems.DIRT_BOTTLE_ITEM;

public class DirtBottleEntity extends ThrownItemEntity {
    private final World world = getWorld();

    public DirtBottleEntity(EntityType<DirtBottleEntity> entityType, World world) {
        super(entityType, world);
    }

    public DirtBottleEntity(World world, LivingEntity owner) {
        super(DIRTBOTTLEENTITY, owner, world);
    }

    public DirtBottleEntity(World world, Vec3d position) {
        super(DIRTBOTTLEENTITY, position.x, position.y, position.z, world);
    }


    @Override
    protected Item getDefaultItem() {
        return DIRT_BOTTLE_ITEM;
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity hit = entityHitResult.getEntity();
        BlockPos bPos = hit.getBlockPos();

        if (!world.getBlockState(bPos).isAir() && !world.getBlockState(bPos).isLiquid()) {
            bPos = bPos.up();
        }
        if (world.getBlockState(bPos).isAir() || world.getBlockState(bPos).isLiquid()) {
            getWorld().setBlockState(
                    bPos,
                    Blocks.DIRT.getDefaultState());
        }

        discard();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult.getType().equals(HitResult.Type.ENTITY)) {
            return;
        }
        BlockPos bPos = new BlockPos(getBlockX(), (int) Math.round(getY()), getBlockZ());
        FallingBlockEntity.spawnFromBlock(world, bPos, Blocks.DIRT.getDefaultState());
        discard();
    }

    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(DIRT_BOTTLE_ITEM);
        NbtCompound tag = new NbtCompound();
        tag.putUuid("EntityUUID", this.getUuid());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }
}