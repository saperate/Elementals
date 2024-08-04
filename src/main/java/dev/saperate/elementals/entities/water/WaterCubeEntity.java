package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.entities.ElementalEntities.WATERCUBE;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterCubeEntity extends AbstractElementalsEntity<PlayerEntity> {

    public WaterCubeEntity(EntityType<WaterCubeEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public WaterCubeEntity(World world, PlayerEntity owner) {
        super(WATERCUBE, world, PlayerEntity.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterCubeEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(WATERCUBE, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.ENTITY_PLAYER_SWIM, 0.25f, 0);
        }

        Entity owner = getOwner();
        if (owner == null || isRemoved()) {
            return;
        }


        if (!owner.isSneaking()) {
            moveEntity(owner);
        }

    }

    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(owner, 3).toVector3f());
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public void collidesWithGround() {
        if (!getEntityWorld().getRegistryKey().equals(World.NETHER) && getWorld().getGameRules().getBoolean(BENDING_GRIEFING)) {
            getWorld().setBlockState(getBlockPos(), Blocks.WATER.getDefaultState());
        }
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.damage(this.getDamageSources().playerAttack((PlayerEntity) getOwner()), 4);
        entity.addVelocity(this.getVelocity().multiply(1));
        discard();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);

    }

    @Override
    public float projectileDeflectionRange() {
        return .5f;
    }

}
