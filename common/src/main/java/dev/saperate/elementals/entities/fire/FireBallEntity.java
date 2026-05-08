package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.misc.FireExplosion;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Level;
import net.minecraft.world.explosion.Explosion;

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
        super(FIREBALL, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public FireBallEntity(Level world, Player owner, double x, double y, double z) {
        super(FIREBALL, world, Player.class);
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

        if (touchingWater && !level().isClientSide) {
            discard();

            Player owner = getOwner();
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
        level().setBlockState(getOnPos(), AbstractFireBlock.getState(level(), getOnPos()));
        FireExplosion explosion = new FireExplosion(level(), getOwner(), getX(), getY(), getZ(), 2.5f, true, Explosion.DestructionType.KEEP, 12 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER, getOwner());
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
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
        this.level().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f, true);
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
