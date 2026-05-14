package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.misc.FireExplosion;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;

import java.util.Objects;

import static dev.saperate.elementals.entities.ElementalEntities.FIREBALL;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireBallEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Boolean> IS_BLUE = SynchedEntityData.defineId(FireBallEntity.class, EntityDataSerializers.BOOLEAN);
    public FireBallEntity(EntityType<FireBallEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public FireBallEntity(Level world, Player owner) {
        super(FIREBALL.get(), world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public FireBallEntity(Level world, Player owner, double x, double y, double z) {
        super(FIREBALL.get(), world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BLUE, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (isInWater() && !level().isClientSide) {
            discard();

            Player owner = getOwner();
            if(owner != null){
                Bender bender = Bender.getBender((ServerPlayer) owner);
                if(bender.currAbility != null){
                    bender.currAbility.onRemove(bender);
                }
            }
            return;
        }

        if (random.nextInt(0, 20) == 6) {
            summonParticles(this, random,
                    isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    0, 1);
            playSound(SoundEvents.FIRE_AMBIENT, 1, 0);

        }

        Entity owner = getOwner();
        if (owner == null || isRemoved()) {
            return;
        }

        if (!owner.isCrouching()) {
            moveEntity();
        }

    }

    private void moveEntity() {
        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(getOwner(), 3).subtract(0,0.5,0).toVector3f());
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
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
        level().setBlockAndUpdate(getOnPos(), BaseFireBlock.getState(level(), getOnPos()));
        FireExplosion explosion = new FireExplosion(level(), getOwner(), getX(), getY(), getZ(), 2.5f, true, Explosion.BlockInteraction.KEEP, 12 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER, getOwner());
        explosion.explode();
        explosion.finalizeExplosion(true);
        discard();
    }

    @Override
    public void onClientRemoval() {
        if(Objects.equals(getRemovalReason(), RemovalReason.KILLED)){
            return;
        }
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.25f, 25);
        this.level().playSound(this, getOnPos(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
    }

    public boolean isBlue() {
        return this.entityData.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getEntityData().set(IS_BLUE, val);
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
