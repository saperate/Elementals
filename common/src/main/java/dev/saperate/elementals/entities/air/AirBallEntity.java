package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.misc.ElementalsSounds;
import dev.saperate.elementals.misc.FireExplosion;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import static dev.saperate.elementals.entities.ElementalEntities.AIRBALL;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirBallEntity extends AbstractElementalsEntity<Player> {

    public AirBallEntity(EntityType<AirBallEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public AirBallEntity(Level world, Player owner) {
        super(AIRBALL.get(), world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public AirBallEntity(Level world, Player owner, double x, double y, double z) {
        super(AIRBALL.get(), world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextInt(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);

            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
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
            moveEntityTowardsGoal(getEntityLookVector(getOwner(),3).subtract(0,0.5,0).toVector3f());
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
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
        FireExplosion explosion = new FireExplosion(level(), this, getX(), getY(), getZ(), 2.5f, false, Explosion.BlockInteraction.KEEP, 8 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER, 4, getOwner());
        explosion.explode();
        explosion.finalizeExplosion(true);
        discard();
    }

    @Override
    public float getMovementSpeed() {
        return 0.1f;
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.25f, 25);
        level().playLocalSound(this.getX(), this.getY(), this.getZ(), WIND_BURST_SOUND_EVENT, SoundSource.BLOCKS, 4.0F, (1.0F + (level().random.nextFloat() - level().random.nextFloat()) * 0.2F) * 0.7F, false);
    }
    @Override
    public float touchGroundFrictionMultiplier() {
        return -1;
    }

}
