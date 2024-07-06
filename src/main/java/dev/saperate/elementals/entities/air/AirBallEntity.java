package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.FireExplosion;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRBALL;
import static dev.saperate.elementals.entities.ElementalEntities.FIREBALL;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirBallEntity extends AbstractElementalsEntity<PlayerEntity> {

    public AirBallEntity(EntityType<AirBallEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public AirBallEntity(World world, PlayerEntity owner) {
        super(AIRBALL, world, PlayerEntity.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public AirBallEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(AIRBALL, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);

            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f);
        }

        Entity owner = getOwner();
        if (owner == null || isRemoved()) {
            return;
        }


        if (!owner.isSneaking()) {
            moveEntity();
        }

    }

    private void moveEntity() {
        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(getOwner(),3).subtract(0,0.5,0).toVector3f());
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public void onHitEntity(Entity entity) {
        onCollision();
    }

    @Override
    public void collidesWithGround() {
        onCollision();
    }

    public void onCollision() {
        FireExplosion explosion = new FireExplosion(getWorld(), getOwner(), getX(), getY(), getZ(), 2.5f, false, Explosion.DestructionType.KEEP, 8, 4, getOwner());
        explosion.collectBlocksAndDamageEntities();
        discard();
    }

    @Override
    public float getMovementSpeed() {
        return 0.1f;
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.25f, 25);
        this.getWorld().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);
    }
}
