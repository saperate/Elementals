package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERHEALING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterHealingEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Float> HEALING = SynchedEntityData.defineId(WaterHealingEntity.class, EntityDataSerializers.FLOAT);

    public WaterHealingEntity(EntityType<WaterHealingEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterHealingEntity(Level world, Player owner) {
        super(WATERHEALING, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterHealingEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERHEALING, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEALING, 1f);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.PLAYER_SWIM, 0.25f, 0);
        }

        Player owner = (Player) getOwner();

        if (owner != null && !isRemoved()) {
            moveEntity(owner);
        }
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.addDeltaMovement(this.getDeltaMovement().scale(0.8f));
        healTarget(entity);
        discard();
    }

    @Override
    public void onTouchEntity(Entity entity) {
        if (tickCount % 20 == 0) {
            healTarget(entity);
        }
    }

    public void healTarget(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.setHealth(living.getHealth() + (getHealing() * (entity.equals(getOwner()) ? 0.5f : 1)) * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        }
    }

    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlEntity(Entity owner) {
        float distance = 3;
        if (owner.isCrouching()) {
            distance = 6;
        }
        Vector3f direction = getEntityLookVector(owner, distance)
                .subtract(0, 0.25f, 0)
                .subtract(position()).toVector3f();
        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setDeltaMovement(0, 0, 0);
        }


        this.addDeltaMovement(new Vec3(direction.x, direction.y, direction.z));
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.level().playSound(this, getOnPos(), SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.25f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);

    }

    public void setHealing(float val) {
        this.entityData.set(HEALING, val);
    }

    public float getHealing() {
        return this.entityData.get(HEALING);
    }

    @Override
    public boolean damagesOnTouch() {
        return true;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}
