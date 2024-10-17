package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.misc.FireExplosion;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;

import static dev.saperate.elementals.entities.ElementalEntities.FIREBALL;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireBallEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Boolean> IS_BLUE = DataTracker.registerData(FireBallEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public FireBallEntity(EntityType<FireBallEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public FireBallEntity(World world, PlayerEntity owner) {
        super(FIREBALL, world, PlayerEntity.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public FireBallEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(FIREBALL, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(IS_BLUE, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (touchingWater) {
            discard();

            PlayerEntity owner = getOwner();
            if(owner != null){
                Bender bender = Bender.getBender((ServerPlayerEntity) owner);
                if(bender.currAbility != null){
                    bender.currAbility.onRemove(bender);
                }
            }
            return;
        }

        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    0, 1);
            playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);

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
            moveEntityTowardsGoal(getEntityLookVector(getOwner(), 3).subtract(0,0.5,0).toVector3f());
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public float getMovementSpeed() {
        return 0.1f;
    }

    @Override
    public void onHitEntity(Entity entity) {
        onCollision();
    }

    @Override
    public void collidesWithGround() {
        onCollision();
    }

    public void onCollision(){
        getWorld().setBlockState(getBlockPos(), AbstractFireBlock.getState(getWorld(), getBlockPos()));
        FireExplosion explosion = new FireExplosion(getWorld(), getOwner(), getX(), getY(), getZ(), 2.5f, true, Explosion.DestructionType.KEEP, 12, getOwner());
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        discard();
    }

    @Override
    public void onRemoved() {
        if(Objects.equals(getRemovalReason(), RemovalReason.KILLED)){
            return;
        }
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.25f, 25);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);
    }

    public boolean isBlue() {
        return this.dataTracker.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getDataTracker().set(IS_BLUE, val);
    }

    @Override
    public float touchGroundFrictionMultiplier() {
        return -1;
    }

    @Override
    public boolean emitsLight() {
        return true;
    }
}
